/**
 * Convert the provided amount of seconds to a human-readable duration (H:MM:SS / MM:SS).
 *
 * @param {number} time
 *      The amount of time to convert.
 * @return {string}
 *      The human-readable format of the provided time.
 */
export function toFormattedDuration(time) {
    const seconds = Math.floor(time); // Drop decimals fast
    const h = (seconds / 3600) | 0;
    const m = ((seconds % 3600) / 60) | 0;
    const s = seconds % 60;

    const pad = (n) => (n < 10 ? '0' : '') + n;

    if (h > 0) {
        return h + ':' + pad(m) + ':' + pad(s);
    }
    return pad(m) + ':' + pad(s);
}
