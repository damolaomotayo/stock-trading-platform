import { IoSearch } from "react-icons/io5";

interface SearchBoxProps {
  className?: string;
  icon?: React.ReactNode;
  placeholder?: string;
}

const SearchBox: React.FC<SearchBoxProps> = ({
  className = "",
  icon,
  placeholder = "Enter you email here",
}) => {
  return (
    <div
      className={`flex items-center border rounded-lg px-3 py-2 gap-2 text-sm max-w-sm w-full ${className}`}
    >
      {icon ? (
        <span className="text-gray-400">{icon}</span>
      ) : (
        <IoSearch className="left-3 top-3 text-gray-400" />
      )}
      <input
        className="outline-none w-full"
        type="text"
        placeholder={placeholder}
      />
    </div>
  );
};

export default SearchBox;
