import Logger from "../Logger.js";
import Trackbar from "../ui/Trackbar.js";
import ActivityTracker from "./ActivityTracker.js";
import {toFormattedDuration} from "../Utils.js";

const languageMap = {
    'jpn': 'Japonais',
    'fre': 'Français'
}

/**
 * @typedef {Object} Track
 * @property {number} id ID of the track
 * @property {string} codec Codec used for the track
 * @property {'video'|'audio'|'subtitle'} type Type of the track
 * @property {string} name Name of the track
 * @property {string|null} label Custom label for the track
 * @property {string|null} language Language of the track
 */

/**
 * @typedef {Object} MediaDescriptor
 * @property {string} mpd Path (from the domain root) pointing toward the MDP metadata file
 * @property {Track[]} tracks Array of track composing the episode
 */


export default class Aniplayer {

    static SETTING_VOLUME = 'aniplayer.volume';

    constructor() {

        this.logger = new Logger('Aniplayer');
        this.shaka = new shaka.Player();
        this.octopus = null;

        this.keymap = {
            ' ': () => this.togglePlay(),
            'ArrowRight': () => this.ui.video.currentTime += 5,
            'ArrowLeft': () => this.ui.video.currentTime -= 5,
            'ArrowUp': () => this.setVolume(this.ui.video.volume + 0.05),
            'ArrowDown': () => this.setVolume(this.ui.video.volume - 0.05),
            'Enter': () => this.toggleFullscreen(),
        }

        this.ui = {
            container: document.getElementById('player'),
            video: document.querySelector('video'),
            playerBar: document.querySelector('#player .player-bar'),
            currentTime: document.querySelector('#player .track-time-current'),
            totalTime: document.querySelector('#player .track-time-total'),
            noMouse: document.querySelector('#player .no-mouse'),
            playBtn: document.querySelector('#player .btn-play'),
            pauseBtn: document.querySelector('#player .btn-pause'),
            settingsBtn: document.querySelector('#player .btn-settings'),
            minimizeBtn: document.querySelector('#player .btn-minimize'),
            maximizeBtn: document.querySelector('#player .btn-maximize'),
            timingBar: document.querySelector('#player .media-timing'),
            volumeBar: document.querySelector('#player .media-volume'),
            timingHandle: document.querySelector('#player .media-timing .handle'),
            volumeHandle: document.querySelector('#player .media-volume .handle'),
            menu: document.querySelector('#player .player-menu'),
            audioList: document.querySelector('#player .audio-list'),
            subsList: document.querySelector('#player .subs-list'),
        }

        this.isHoverControls = false;
        this.activityTracker = new ActivityTracker(this.ui.container, 3);

        this.trackbars = {
            media: new Trackbar(this.ui.timingBar, {
                onChange: value => {
                    this.ui.video.fastSeek(value)
                    this.ui.video.focus();
                }
            }),
            volume: new Trackbar(this.ui.volumeBar, {
                onUpdate: value => this.ui.video.volume = value,
                onChange: value => this.setVolume(value)
            }),
        }

        this.trackbars.volume.maximum = 1;

        /**
         * @type {{audio: HTMLElement[], subs: HTMLElement[]}}
         */
        this.menuEntries = {
            audio: [],
            subs: []
        }

        setInterval(() => this.clock(), 100)

        this.onPause();
        this.onExitFullscreen();

        this.ui.video.addEventListener('playing', () => this.onPlay());
        this.ui.video.addEventListener('pause', () => this.onPause());
        this.ui.playBtn.addEventListener('click', () => this.play());
        this.ui.pauseBtn.addEventListener('click', () => this.pause());
        this.ui.maximizeBtn.addEventListener('click', () => this.enterFullscreen());
        this.ui.minimizeBtn.addEventListener('click', () => this.exitFullscreen());
        this.ui.settingsBtn.addEventListener('click', () => this.toggleSettings());
        this.ui.noMouse.addEventListener('dblclick', () => this.toggleFullscreen())

        this.ui.noMouse.addEventListener('click', () => {
            if (this.isSettingOpened()) {
                this.hideSettings();
            } else {
                this.togglePlay();
            }
        });

        this.ui.noMouse.addEventListener('mousemove', () => this.activityTracker.signalActivity());
        this.ui.playerBar.addEventListener('mouseenter', () => this.isHoverControls = true);
        this.ui.playerBar.addEventListener('mouseleave', () => this.isHoverControls = false);

        this.ui.container.addEventListener('keydown', e => {
            if (this.keymap[e.key]) {
                this.keymap[e.key]();
            }
        });

        /**
         * @type {Track[]}
         */
        this.tracks = [];
    }

    clock() {
        this.activityTracker.setLock(this.ui.video.paused || this.isSettingOpened() || this.isHoverControls);
        this.trackbars.media.maximum = this.ui.video.duration;
        this.trackbars.media.value = this.ui.video.currentTime;

        this.ui.totalTime.innerText = toFormattedDuration(isNaN(this.ui.video.duration) ? 0 : this.ui.video.duration);
        this.ui.currentTime.innerText = toFormattedDuration(this.ui.video.currentTime);
    }

    setVolume(volume) {
        this.ui.video.volume = volume;
        localStorage.setItem(Aniplayer.SETTING_VOLUME, `${volume}`);
        this.activityTracker.signalActivity();
        this.ui.video.focus();
    }

    async init() {
        this.logger.log('init()', 'Starting initialization...')
        await this.shaka.attach(this.ui.video);
        this.ui.video.volume = parseFloat(localStorage.getItem('aniplayer.volume') ?? '1');
        this.logger.log('init()', 'Player is ready.')
    }

    onPause() {
        this.ui.playBtn.classList.remove('hidden');
        this.ui.pauseBtn.classList.add('hidden');
        this.ui.video.focus();
        this.activityTracker.signalActivity();
        this.ui.video.focus();
    }

    onPlay() {
        this.ui.playBtn.classList.add('hidden');
        this.ui.pauseBtn.classList.remove('hidden');
        this.activityTracker.signalActivity();
        this.ui.video.focus();
    }

    onEnterFullscreen() {
        this.ui.maximizeBtn.classList.add('hidden');
        this.ui.minimizeBtn.classList.remove('hidden');
        this.activityTracker.signalActivity();
        this.ui.video.focus();
    }

    onExitFullscreen() {
        this.ui.maximizeBtn.classList.remove('hidden');
        this.ui.minimizeBtn.classList.add('hidden');
        this.activityTracker.signalActivity();
        this.ui.video.focus();
    }

    /**
     * @return {Track[]}
     */
    getAvailableSubtitles() {
        return this.tracks.filter(track => track.type === 'subtitle')
    }

    /**
     * @param {MediaDescriptor} descriptor
     * @returns {Promise<void>}
     */
    async load(descriptor) {
        this.logger.log('load()', 'Loading descriptor', descriptor)
        this.tracks = descriptor.tracks;
        await this.shaka.load(descriptor.mpd);

        this.logger.log('load()', 'Building tracks menu')
        this.buildMenuEntries();

        const subs = this.getAvailableSubtitles();
        if (subs.length) {
            const sub = subs[0];
            this.logger.log('load()', 'Using subtitle', sub);
            this.useSubtitles(subs[0].id);
        } else {
            this.logger.log('load()', 'No subtitles available for this episode.');
        }
    }

    buildMenuEntries() {
        this.menuEntries.audio.forEach(node => node.remove());
        this.menuEntries.subs.forEach(node => node.remove());
        this.menuEntries.audio = [];
        this.menuEntries.subs = [];

        for (let track of this.shaka.getAudioTracks()) {
            const element = document.createElement('li');
            if (track.active) element.classList.add('active');
            element.innerText = languageMap[track.originalLanguage] ?? track.originalLanguage;
            element.addEventListener('click', () => {
                this.shaka.selectAudioLanguage(track.language);
                this.menuEntries.audio.forEach(entry => entry.classList.remove('active'));
                element.classList.add('active');
            });

            this.menuEntries.audio.push(element);
            this.ui.audioList.append(element);
        }

        const noSub = document.createElement('li');
        noSub.innerText = 'Désactivés';
        noSub.addEventListener('click', () => {
            this.useSubtitles(null);
            this.menuEntries.subs.forEach(entry => entry.classList.remove('active'));
            noSub.classList.add('active');
        });

        this.menuEntries.subs.push(noSub);
        this.ui.subsList.append(noSub);

        for (let track of this.getAvailableSubtitles()) {
            const element = document.createElement('li');
            element.innerText = track.label ?? languageMap[track.language] ?? track.language;
            element.setAttribute('track-id', `${track.id}`);
            element.addEventListener('click', () => {
                this.useSubtitles(track.id);
            });
            this.menuEntries.subs.push(element);
            this.ui.subsList.append(element);
        }
    }

    /**
     * @param {number|null} trackId
     */
    useSubtitles(trackId) {
        this.menuEntries.subs.forEach(node => node.classList.remove('active'))

        if (trackId == null) {
            this.disposeOctopus();
            this.menuEntries.subs[0].classList.add('active');
            return;
        }

        const track = this.tracks.find(entry => entry.id === trackId);
        if (track.type !== 'subtitle') throw new Error(`Track ${track.id} is not a subtitle track.`)
        const url = `/media/subs/${track.id}`;
        this.logger.log('useSubtitles()', 'Trying to load', url)

        this.disposeOctopus();

        this.logger.log('useSubtitles()', 'Creating Octopus instance...');

        this.octopus = new SubtitlesOctopus({
            video: this.ui.video,
            subUrl: url,
            fonts: ['/assets/fonts/trebuc.ttf'],
            workerUrl: '/assets/libs/subtitles-octopus/subtitles-octopus-worker.js',
            legacyWorkerUrl: '/assets/libs/subtitles-octopus/subtitles-octopus-worker-legacy.js',
            renderMode: 'wasm-blend',
            targetFps: 24,
            onReady: () => this.logger.log('useSubtitles()', 'Octopus ready.'),
            onError: e => this.logger.error('useSubtitles()', e)
        });

        for (let sub of this.menuEntries.subs) {
            if (sub.getAttribute('track-id') === `${trackId}`) {
                sub.classList.add('active');
                break;
            }
        }

    }

    dispose() {
        this.disposeOctopus();
        this.pause();
    }

    disposeOctopus() {
        if (this.octopus) {
            this.logger.log('dispose()', 'Disposing existing Octopus instance...');
            this.octopus.dispose();
            this.octopus = null;
        }
    }

    play() {
        if (!this.shaka.isFullyLoaded()) {
            return;
        }

        this.ui.video.play().then();
        this.hideSettings();
    }

    pause() {
        this.ui.video.pause();
        this.hideSettings();
    }

    togglePlay() {
        if (this.ui.video.paused) {
            this.play();
        } else {
            this.pause();
        }
    }

    enterFullscreen() {
        this.hideSettings();
        this.ui.container.requestFullscreen().then(() => this.onEnterFullscreen());
    }

    exitFullscreen() {
        this.hideSettings();
        document.exitFullscreen().then(() => this.onExitFullscreen());
    }

    toggleFullscreen() {
        const isFullscreen = !!document.fullscreenElement;
        if (isFullscreen) {
            this.exitFullscreen()
        } else {
            this.enterFullscreen();
        }
    }

    showSettings() {
        this.ui.menu.classList.remove('hidden');
    }

    hideSettings() {
        this.ui.menu.classList.add('hidden');
        this.ui.video.focus();
    }

    isSettingOpened() {
        return !this.ui.menu.classList.contains('hidden');
    }

    toggleSettings() {
        if (this.isSettingOpened()) {
            this.hideSettings();
        } else {
            this.showSettings();
        }
    }
}

