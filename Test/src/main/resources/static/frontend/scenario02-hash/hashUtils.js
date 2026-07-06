function decodeHashParam(raw) {
    try {
        return decodeURIComponent(raw);
    } catch (e) {
        return raw;
    }
}

window.HashUtils = { decodeHashParam };
