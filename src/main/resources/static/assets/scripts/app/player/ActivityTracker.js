/**
 * Class allowing to keep track if the user has been showing any sign of activity. It is very useful for video players
 * for hiding controls after a defined amount of time without activity.
 *
 * This merely hold the activity timer and add/remove the class depending on it. The manager is not responsible to
 * determine if the user is currently active or not. To signal the manager that the user is active, please use the
 * ***signalActivity()*** method.
 */
export default class ActivityTracker {

    /**
     * Create a new Activity Tracker
     *
     * @param {HTMLElement} element
     *      HTML Element that will receive or loose the class 'inactive' depending on the
     *      current state of the user activity.
     * @param {number} timeout
     *      Amount of time, in second, after which the user will be considered as inactive.
     */
    constructor(element, timeout) {
        this._element = element;
        this._inactiveSince = 0;
        this._lock = 0;
        this._timeout = timeout * 10;
        this._timer = setInterval(() => this._tick(), 100);
    }

    /**
     * Lock this Activity Tracker. A locked activity tracker will consider the user as active regardless of the
     * inactivity timer. This can prove useful if the user is hovering certain parts of the UI to avoid it being
     * wrongfully hidden.
     */
    lock() {
        this._lock = true;
    }

    /**
     * Unlock this Activity Tracker. An unlocked activity tracker will check user inactivity as normal.
     */
    unlock() {
        this._lock = false;
    }

    /**
     * Vanity method to change the lock state of this Activity Tracker directly taking the result of a boolean
     * expression.
     *
     * @param {boolean} value
     *      The new lock state. True if locked, false otherwise.
     */
    setLock(value) {
        this._lock = value;
    }

    /**
     * Signal the manager that the user was active and resets the timeout.
     */
    signalActivity() {
        this._inactiveSince = 0;
        this._element.classList.remove('inactive');
    }

    /**
     * Dispose this ActivityTracker
     */
    dispose() {
        if (this._timer) throw new Error('This element has already been disposed of.')
        clearInterval(this._timer);
        this._timer = -1;
    }

    /**
     * @private
     */
    _tick() {
        if (this._lock) {
            this.signalActivity()
            return;
        }

        this._inactiveSince++;

        if (this._inactiveSince >= this._timeout) {
            this._element.classList.add('inactive');
        }
    }

}
