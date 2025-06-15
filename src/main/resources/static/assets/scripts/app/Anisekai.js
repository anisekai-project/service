import Aniplayer from "./player/Aniplayer.js";
import Collapse from "./Collapse.js";

export default class Anisekai {

    constructor() {

        if (document.getElementById('player-container')) {
            this.player = new Aniplayer();
        } else {
            this.player = null;
        }

        this.elements = {
            tree: document.getElementById('tree-view'),
            search: document.getElementById('search-input'),
            empty: document.querySelector('[data-if="empty"]'),
            playerContainer: document.getElementById('player-container'),
            backBtn: document.querySelector('#player-container .back-btn')
        }

        /**
         * @type {Collapse[]}
         */
        this.data = [];

        this.elements.tree.querySelectorAll(':scope > details').forEach(node => this.data.push(new Collapse(node)));
        this.elements.search.addEventListener('input', (ev) => this.search(ev.target.value));

        if (this.player) {
            this.elements.backBtn.addEventListener('click', () => this.closeMedia());

            document.querySelectorAll('[data-play]').forEach(node => {
                node.addEventListener('click', () => {
                    const mediaId = parseInt(node.getAttribute('data-play'));
                    this.playMedia(mediaId).then();
                })
            })
        }

    }

    /**
     * Initialize the app.
     * @returns {Promise<void>}
     */
    async init() {
        this.search('')
        if (this.player) {
            await this.player.init();
        }
        feather.replace();
    }

    /**
     * Execute a search using the provided search string.
     * @param {string} filter
     */
    search(filter) {
        let hasResults = false;

        this.data.forEach(anime => {
            if (filter === '') {
                // Quick filter
                anime.show();
                hasResults = true;
                return;
            }

            const display = anime.name.toLowerCase().indexOf(filter.toLowerCase()) > -1;

            if (display) {
                anime.show();
                hasResults = true;
            } else {
                anime.hide();
            }
        });

        if (hasResults) {
            this.elements.empty.classList.add('hidden');
        } else {
            this.elements.empty.classList.remove('hidden');
        }
    }

    /**
     * Play a media matching the provided ID.
     * @param {number} mediaId
     * @returns {Promise<MediaDescriptor>}
     */
    async playMedia(mediaId) {
        if (this.player == null) {
            throw new Error('No player available.')
        }

        const response = await fetch(`/media/describe/${mediaId}`);
        const descriptor = await response.json();
        await this.player.load(descriptor);

        this.elements.empty.classList.add('hidden');
        this.elements.tree.classList.add('hidden');
        this.elements.playerContainer.classList.remove('hidden');
    }

    closeMedia() {
        if (this.player == null) {
            throw new Error('No player available.')
        }

        this.elements.tree.classList.remove('hidden');
        this.elements.playerContainer.classList.add('hidden');
        this.player.dispose();
        this.search(this.elements.search.value);
    }
}
