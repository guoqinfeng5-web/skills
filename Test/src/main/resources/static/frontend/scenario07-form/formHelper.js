function parsePrefillData(query) {
    const result = {};
    if (query.title) {
        result.title = query.title;
    }
    if (query.content) {
        try {
            result.content = atob(query.content);
        } catch (e) {
            result.content = query.content;
        }
    }
    if (query.note) {
        try {
            result.note = atob(query.note);
        } catch (e) {
            result.note = query.note;
        }
    }
    return result;
}

window.FormHelper = { parsePrefillData };
