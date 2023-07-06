class Collapse {

    constructor(el) {
        this.el = el;

        this.id = this.el.id;
        this.name = this.el.querySelector('summary').innerText;
    }

    hide() {
        this.el.classList.add('hidden')
    }

    show() {
        this.el.classList.remove('hidden')
    }
}


const tree = document.getElementById('tree-view');
const download = document.getElementById('download');
const search = document.getElementById('search-input');
const modal = document.getElementById('modal');
const player = document.getElementById('video-player');
const noResult = document.querySelector('[data-if="empty"]');

const backdrop = modal.querySelector('.backdrop');
const plyr = new Plyr(player, {
    disableContextMenu: false,
    captions: {
        active: true,
        language: 'fr',
    },
    global: true,
    iconUrl: 'assets/plyr/plyr.svg',
});

/**
 * @param {string} uri
 * @param {{uri: String}[]} subtitles
 */
const playEpisode = (uri, subtitles) => {

    const options = {
        type: 'video',
        sources: [
            {
                src: `/${uri}/video`,
                type: 'video/mp4',
            },
        ],
        tracks:  subtitles.map(obj => {
            return {
                kind:    'captions',
                label:   'French',
                srclang: 'fr',
                src:     `/${uri}/subs/${obj}`,
                default: true,
            };
        }),
    };

    console.log('playEpisode()', options);

    plyr.source = options;

    plyr.captions.enabled = true;
    download.href = `/${uri}/video`;
    modal.classList.remove('hidden');
};

const closePlayer = () => {
    modal.classList.add('hidden');
    plyr.pause();
};

const data = [];


const doFilter = (filter) => {
    console.log('doFilter()', {filter});

    let shown = 0;
    let hidden = 0;

    data.forEach(anime => {
        const display = anime.name.toLowerCase().indexOf(filter.toLowerCase()) > -1;

        if (display) {
            shown++;
            anime.show();
        } else {
            hidden++;
            anime.hide();
        }
    });

    console.log('doFilter()', {
        shown,
        hidden,
    });

    if (shown === 0) {
        noResult.classList.remove('hidden');
    } else {
        noResult.classList.add('hidden');
    }
};

const castShadow = (r) => {
    const color = '#2C2C2C'; /* white outline */
    const n = Math.ceil(2 * Math.PI * r); /* number of shadows */
    let str = '';
    for (let i = 0; i < n; i++) { /* append shadows in n evenly distributed directions */
        const theta = 2 * Math.PI * i / n;
        str += (r * Math.cos(theta)) + 'px ' + (r * Math.sin(theta)) + 'px 0 ' + color + (i === n - 1 ? '' : ',');
    }

    const captions = document.querySelector('.plyr__captions');
    if (captions) {
        captions.style.textShadow = str;
    }
};

(() => {

    // Events
    search.addEventListener('input', (ev) => doFilter(ev.target.value));
    backdrop.addEventListener('click', () => closePlayer());

    // Captions font size hack - Make it relative to player size.
    const plyrPlayer = document.querySelector('.player > .plyr');
    const observer = new ResizeObserver(entries => {
        const element = entries[0];
        const sizeRatio = 38 / 1920; // Arbitrary.
        const shadowRatio = 3 / 1920;
        const fontSize = element.contentRect.width * sizeRatio;
        const shadowSize = element.contentRect.width * shadowRatio;
        modal.style.setProperty('--fsize', `${fontSize}px`);
        castShadow(shadowSize);
    });

    observer.observe(plyrPlayer);
})();

(() => {
    tree.querySelectorAll(':scope > details').forEach(node => {
        data.push(new Collapse(node));
    });

    tree.querySelectorAll('.file').forEach(node => {
        node.addEventListener('click', () => {

            const idpath = [node.id];
            let nextNode = node;

            while ((nextNode = nextNode.parentElement.closest('details')) !== null) {
                idpath.push(nextNode.id);
            }

            const path = idpath.reverse().join('/');
            const subs = [];

            // Retrieve subs
            node.querySelectorAll('meta[name="subtitle"]').forEach(sub => {
                subs.push(sub.getAttribute('content'));
            })


            playEpisode(path, subs);
        })
    });

    doFilter('');
})();


// Expose
window.data = data;
window.plyr = plyr;
