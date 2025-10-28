import { IoSearch } from "react-icons/io5";

const SearchBox = () => {
  return (
    <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2 gap-2 text-sm max-w-sm w-full">
      <IoSearch className="left-3 top-3 text-gray-400" />
      <input
        className="outline-none w-full"
        type="text"
        placeholder="Enter your email here"
      />
    </div>
  );
};

export default SearchBox;
