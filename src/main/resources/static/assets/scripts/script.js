(async () => {
    const search   = document.getElementById('search-input');
    const modal    = document.getElementById('modal');
    const player   = document.getElementById('video-player');
    const download = document.getElementById('download');

    const backdrop = modal.querySelector('.backdrop');

    const plyr = new Plyr(player, {
        disableContextMenu: false,
        captions:           {
            active:   true,
            language: 'fr',
        },
        global:             true,
    });

    const response = await fetch('/api/v1/fs');
    const data     = await response.json();

    const render = (filter) => {
        build(data.filter(anime => anime.name.toLowerCase().indexOf(filter.toLowerCase()) > -1));

        document.querySelectorAll('.container').forEach(node => {
            const parent = node.parentNode;
            parent.classList.add('collapse');

            const p = node.parentNode.querySelector(':scope > p');

            if (p) {
                p.addEventListener('click', () => {
                    if (parent.classList.contains('collapse')) {
                        parent.classList.remove('collapse');
                    } else {
                        parent.classList.add('collapse');
                    }
                });
            }
        });

        document.querySelectorAll('a').forEach(node => {

            if (node.id === 'download') {
                return;
            }

            node.addEventListener('click', (ev) => {

                ev.preventDefault();

                /*const subtitles = ev.target.href.replace('.mkv', '-2-fre.vtt');*/

                plyr.source = {
                    type:    'video',
                    sources: [
                        {
                            src:  ev.target.href,
                            type: 'video/mp4',
                        },
                    ],
                    /*tracks:  [
                        {
                            kind:    'captions',
                            label:   'French',
                            srclang: 'fr',
                            src:     subtitles,
                            default: true,
                        },
                    ],*/
                };

                //plyr.captions.enabled = true;
                download.href         = ev.target.href;

                modal.classList.remove('hidden');
            });
        });
    };

    // Do initial render
    render('');

    // Search input to filter list items
    search.addEventListener('input', (ev) => {
        render(ev.target.value);
    });

    // Close modal when clicking on backdrop
    backdrop.addEventListener('click', () => {
        modal.classList.add('hidden');
        plyr.pause();
    });


    // Captions font size hack - Make it relative to player size.
    const plyrPlayer = document.querySelector('.player > .plyr');
    const observer   = new ResizeObserver(entries => {
        const element   = entries[0];
        const sizeRatio = 38 / 1920; // Arbitrary.
        const fontSize  = element.contentRect.width * sizeRatio;
        modal.style.setProperty('--fsize', `${fontSize}px`);
    });

    observer.observe(plyrPlayer);
})();

function build(data) {
    const container = document.getElementById('tree-view');
    container.innerHTML = '';

    if (data.length === 0) {
        const note = document.createElement('div');
        note.classList.add('empty');
        note.innerText = 'Aucun résultat';
        container.appendChild(note);
        return;
    }

    data.sort(function (a, b) {
        return a.name < b.name;
    });


    data.forEach(anime => {
        container.appendChild(createAnimeElement(anime));
    });
}

/**
 * @param {string} name
 * @param {*[]} groups
 * @return {Element}
 */
function createAnimeElement({
                                name,
                                groups,
                            }) {
    const animeEl = document.createElement('div');
    animeEl.classList.add('anime');
    const nameEl = document.createElement('p');

    nameEl.innerText = name;
    animeEl.appendChild(nameEl);

    const groupsEl = document.createElement('div');
    groupsEl.classList.add('container');

    groups.forEach(group => {
        groupsEl.appendChild(createGroupElement(group));
    });

    animeEl.appendChild(groupsEl);
    return animeEl;
}

/**
 * @param {string} name
 * @param {string=} uri
 * @param {*[]} episodes
 * @return {Element}
 */
function createGroupElement({
                                name,
                                uri,
                                episodes,
                            }) {
    const groupEl = document.createElement('div');
    groupEl.classList.add('group');

    if (uri) {
        const nameEl     = document.createElement('a');
        nameEl.innerText = name;
        nameEl.href      = uri;
        groupEl.appendChild(nameEl);
    } else {
        const nameEl     = document.createElement('p');
        nameEl.innerText = name;
        groupEl.appendChild(nameEl);
        const episodesEl = document.createElement('div');
        episodesEl.classList.add('container');

        episodes.forEach(episode => {
            episodesEl.appendChild(createEpisodeElement(episode));
        });
        groupEl.appendChild(episodesEl);
    }

    return groupEl;
}

/**
 * @param {string} name
 * @param {string} uri
 * @return {Element}
 */
function createEpisodeElement({
                                  name,
                                  uri,
                              }) {

    const episodeEl = document.createElement('div');
    const nameEl    = document.createElement('a');

    episodeEl.classList.add('episode');
    nameEl.innerText = name;
    nameEl.href      = uri;
    nameEl.setAttribute('data-mode', 'watch')
    episodeEl.appendChild(nameEl);

    return episodeEl;
}
