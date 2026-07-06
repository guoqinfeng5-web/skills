(function () {
    const params = new URLSearchParams(location.search);
    const searchTerm = params.get("q") || "";

    function highlightText(text, term) {
        if (!term) return text;
        const regex = new RegExp("(" + term.replace(/[.*+?^${}()|[\]\\]/g, "\\$&") + ")", "gi");
        return text.replace(regex, "<mark>$1</mark>");
    }

    function renderFromBackend(htmlSnippet) {
        const container = document.getElementById("searchResults");
        container.innerHTML = "<p>搜索词: <strong>" + searchTerm + "</strong></p>";
        container.innerHTML += "<div class=\"backend-snippet\">" + htmlSnippet + "</div>";
    }

    function renderLocalFallback() {
        const mockResults = [
            { title: "Java 编程入门", description: "适合初学者的 Java 教程" },
            { title: "Spring Boot 实战", description: "企业级应用开发指南" }
        ];
        const container = document.getElementById("searchResults");
        let html = "<p>搜索词: <strong>" + searchTerm + "</strong> (本地回退)</p><ul>";
        mockResults.forEach(function (item) {
            html += "<li><h3>" + highlightText(item.title, searchTerm) + "</h3>";
            html += "<p>" + highlightText(item.description, searchTerm) + "</p></li>";
        });
        html += "</ul>";
        container.innerHTML = html;
    }

    function loadSearch() {
        if (!searchTerm) {
            document.getElementById("searchResults").innerHTML = "<p>请输入 ?q= 参数</p>";
            return;
        }

        fetch("/portal/api/search-snippet?q=" + encodeURIComponent(searchTerm), {
            credentials: "include"
        })
        .then(function (resp) { return resp.text(); })
        .then(renderFromBackend)
        .catch(renderLocalFallback);
    }

    loadSearch();
})();
