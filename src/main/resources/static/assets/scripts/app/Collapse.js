
export default class Collapse {

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
