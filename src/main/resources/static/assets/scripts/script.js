const templates = {
    collapse: document.getElementById('collapse'),
    file:     document.getElementById('file'),
};

const tree     = document.getElementById('tree-view');
const download = document.getElementById('download');
const search   = document.getElementById('search-input');
const modal    = document.getElementById('modal');
const player   = document.getElementById('video-player');
const noResult = document.querySelector('[data-if="empty"]');

const backdrop = modal.querySelector('.backdrop');
const plyr     = new Plyr(player, {
    disableContextMenu: false,
    captions:           {
        active:   true,
        language: 'fr',
    },
    global:             true,
    iconUrl:            'assets/plyr/plyr.svg',
});

const data = [];

/**
 * @param {string} uri
 * @param {{uri: String}[]} subtitles
 */
const playEpisode = (uri, subtitles) => {

    const options = {
        type:    'video',
        sources: [
            {
                src:  uri,
                type: 'video/mp4',
            },
        ],
        tracks:  subtitles.map(obj => {
            return {
                kind:    'captions',
                label:   'French',
                srclang: 'fr',
                src:     obj.uri,
                default: true,
            };
        }),
    };

    console.log('playEpisode()', options);

    plyr.source = options;

    plyr.captions.enabled = true;
    download.href         = uri;
    modal.classList.remove('hidden');
};

const closePlayer = () => {
    modal.classList.add('hidden');
    plyr.pause();
};

class Collapse {

    constructor(id, name) {
        this.id       = id;
        this.name     = name;
        this.children = [];
        this.el       = templates.collapse.content.cloneNode(true);

        this.el.querySelector('[data-bind="instance"]').setAttribute('data-instance', this.id);
        this.el.querySelector('[data-bind="name"]').innerText = name;
    }

    reselect() {
        this.el = document.querySelector(`[data-instance="${this.id}"]`);
    }

    add(item) {
        this.children.push(item);
        this.el.querySelector('[data-bind="children"]').appendChild(item.el);
        item.reselect();
    }

    hide() {
        this.el.classList.add('hidden');
    }

    show() {
        this.el.classList.remove('hidden');
    }

}

class File {

    constructor(id, name, uri, subtitles) {
        this.id = id;
        this.el = templates.file.content.cloneNode(true);

        this.el.querySelector('[data-bind="name"]').innerText = name;
        this.el.querySelector('[data-bind="instance"]').setAttribute('data-instance', this.id);

        this.el.querySelector('[data-instance]').addEventListener('click', () => {
            playEpisode(uri, subtitles);
        });
    }

    reselect() {
        this.el = document.querySelector(`[data-instance="${this.id}"]`);
    }
}

const doFilter = (filter) => {
    console.log('doFilter()', {filter});

    let shown  = 0;
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
    const color = '#2c2c2c'; /* white outline */
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
    const observer   = new ResizeObserver(entries => {
        const element   = entries[0];
        const sizeRatio = 38 / 1920; // Arbitrary.
        const shadowRatio = 3 / 1920;
        const fontSize  = element.contentRect.width * sizeRatio;
        const shadowSize = element.contentRect.width * shadowRatio;
        modal.style.setProperty('--fsize', `${fontSize}px`);
        castShadow(shadowSize);
    });

    observer.observe(plyrPlayer);
})();

(async () => {

    console.time('HTTP Query');

    const response = await fetch('/api/v1/fs');
    const rawData  = await response.json();

    console.timeEnd('HTTP Query');

    console.time('Objectifying');
    rawData.forEach(anime => {
        const animeInstance = new Collapse(anime.id, anime.name);

        anime.groups.forEach(group => {
            const groupInstance = new Collapse(group.id, group.name);

            group.episodes.forEach(episode => {
                const episodeInstance = new File(episode.id, episode.name, episode.uri, episode.subtitles);
                groupInstance.add(episodeInstance);
            });

            animeInstance.add(groupInstance);
        });

        anime.files.forEach(episode => {
            const episodeInstance = new File(episode.id, episode.name, episode.uri, episode.subtitles);
            animeInstance.add(episodeInstance);
        });

        data.push(animeInstance);
    });
    console.timeEnd('Objectifying');

    console.time('Rendering');
    data.forEach(anime => {
        tree.appendChild(anime.el);
        anime.reselect();
    });
    console.timeEnd('Rendering');

    doFilter('');
})();


// Expose
window.data = data;
window.plyr = plyr;
