import React, { useState, useEffect } from "react";
import { searchProducts } from "./searchApi";

const highlightText = (text, term) => {
  if (!term) return text;
  const regex = new RegExp(`(${term})`, "gi");
  const highlighted = text.replace(regex, '<mark class="highlight">$1</mark>');
  return highlighted;
};

const SearchResult = ({ searchTerm }) => {
  const [results, setResults] = useState([]);

  useEffect(() => {
    searchProducts(searchTerm).then(setResults);
  }, [searchTerm]);

  return (
    <div className="search-results">
      {results.map((item) => (
        <div key={item.id} className="result-item">
          <p
            dangerouslySetInnerHTML={{
              __html: highlightText(item.title, searchTerm),
            }}
          />
          <p
            dangerouslySetInnerHTML={{
              __html: highlightText(item.description, searchTerm),
            }}
          />
        </div>
      ))}
    </div>
  );
};

export default SearchResult;
