/**
 * Callback simply receiving a single number as parameter.
 *
 * @callback NumberCallback
 * @param {number} value The value.
 */

/**
 * Class allowing to easily add customizable trackbars into your web application. This class provide only the core logic
 * and will not create any HTML Element.
 *
 * This class has few minimum expectations:
 *
 * 1. The main element will carry the '--progress' CSS property, letting the CSS decide how to display the component.
 *
 * 2. A child element with the class 'bar' is present within the main element. It will be the one handling the click
 *      and mouse drag of the user, usually representing the ***actual*** progress visually.
 */
export default class Trackbar {

    /**
     * Create a new Trackbar.
     *
     * @param {HTMLElement} element
     *      The HTML Element carrying the '--progress' CSS property.
     * @param {NumberCallback=} onChange
     *      Callback called when the user simply click on the track or finish dragging it, effectively 'commiting'
     *      the value.
     * @param {NumberCallback=} onUpdate
     *      Callback called when the user is actively changing the value. Please keep in mind that no heavy processing
     *      should be done in this callback as it will be called as frequently as the 'mousemove' event permits it.
     */
    constructor(element, {onChange, onUpdate}) {
        this._element = element;
        this._bar = element.querySelector('.bar');

        this._dragging = false;
        this._minimum = 0;
        this._maximum = 0;
        this._value = 0;
        this._override = -1;

        this._onChange = onChange ?? (() => {
        });
        this._onUpdate = onUpdate ?? (() => {
        });

        this._bar.addEventListener('mousedown', e => this._onDragStart(e));
    }

    /**
     * @return {number}
     */
    get minimum() {
        return this._minimum;
    }

    /**
     * @param {number} value
     */
    set minimum(value) {
        this._minimum = value;
        this._refresh()
    }

    /**
     * @return {number}
     */
    get maximum() {
        return this._maximum;
    }

    /**
     * @param {number} value
     */
    set maximum(value) {
        this._maximum = value;
        this._refresh()
    }

    /**
     * @return {number}
     */
    get value() {
        return this._value;
    }

    /**
     * @param {number} value
     */
    set value(value) {
        this._value = value;
        this._refresh()
    }

    /**
     * @private
     */
    _getValueFromEvent(e) {
        const rect = this._bar.getBoundingClientRect();
        const x = Math.min(Math.max(0, e.clientX - rect.left), rect.width);
        const ratio = x / rect.width;
        return this._minimum + ratio * (this._maximum - this._minimum);
    }

    /**
     * @private
     */
    _onDragStart(event) {
        this._dragging = true;

        window.addEventListener('mousemove', this._onDragBound = e => this._onDrag(e));
        window.addEventListener('mouseup', this._onDragEndBound = e => this._onDragEnd(e));

        const value = this._getValueFromEvent(event);
        this._onUpdate(value);
    }

    /**
     * @private
     */
    _onDrag(event) {
        if (!this._dragging) return;
        const value = this._getValueFromEvent(event);
        this._onUpdate(value);
        this._override = value;
        this._refresh();
    }

    /**
     * @private
     */
    _onDragEnd(event) {
        if (!this._dragging) return;
        this._dragging = false;

        window.removeEventListener('mousemove', this._onDragBound);
        window.removeEventListener('mouseup', this._onDragEndBound);

        const value = this._getValueFromEvent(event);
        this._onUpdate(value);
        this._onChange(value);
        this._override = -1;
    }

    /**
     * @private
     */
    _refresh() {

        const targetValue = this._override > -1 ? this._override : this._value;
        const range = this._maximum - this._minimum;

        if (isNaN(range) || range === 0) {
            this._element.style.setProperty('--progress', '0%');
            return;
        }

        const progress = ((targetValue - this._minimum) / range) * 100;
        this._element.style.setProperty('--progress', `${progress}%`);
    }
}
