import React from "react";
import { useSearchParams } from "react-router-dom";
import SearchResult from "./SearchResult";

const SearchComponent = () => {
  const [searchParams] = useSearchParams();
  const query = searchParams.get("q") || "";

  return (
    <div className="search-page">
      <h1>搜索结果</h1>
      <SearchResult searchTerm={query} />
    </div>
  );
};

export default SearchComponent;
