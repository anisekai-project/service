import Aniplayer from "./player/Aniplayer.js";
import Collapse from "./Collapse.js";

export default class Anisekai {

    constructor() {

        this.player = new Aniplayer();

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
        this.elements.backBtn.addEventListener('click', () => this.closeMedia());

        document.querySelectorAll('[data-play]').forEach(node => {
            node.addEventListener('click', () => {
                const mediaId = parseInt(node.getAttribute('data-play'));
                this.playMedia(mediaId).then();
            })
        })
    }

    /**
     * Initialize the app.
     * @returns {Promise<void>}
     */
    async init() {
        this.search('')
        await this.player.init();
        feather.replace();
    }

    /**
     * Execute a search using the provided search string.
     * @param {string} filter
     */
    search(filter) {
        let hasResults = false;

        this.data.forEach(anime => {
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
        const response = await fetch(`/media/describe/${mediaId}`);
        const descriptor = await response.json();
        await this.player.load(descriptor);

        this.elements.empty.classList.add('hidden');
        this.elements.tree.classList.add('hidden');
        this.elements.playerContainer.classList.remove('hidden');
    }
D
    closeMedia() {
        this.elements.tree.classList.remove('hidden');
        this.elements.playerContainer.classList.add('hidden');
        this.player.dispose();
        this.search(this.elements.search.value);
    }
}
