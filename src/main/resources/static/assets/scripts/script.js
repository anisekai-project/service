import Anisekai from "./app/Anisekai.js";

(async () => {
    const anisekai = new Anisekai();
    await anisekai.init()

    // Expose JS API
    window.anisekai = anisekai
})();
