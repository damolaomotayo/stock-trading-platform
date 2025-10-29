import { FaRegEnvelope } from "react-icons/fa6";
import { LuBellRing } from "react-icons/lu";
import SearchBox from "../SearchBox/SearchBox";

const TopNav = () => {
  return (
    <div className="flex justify-between items-center p-4 bg-white shadow-md">
      <SearchBox className="border-gray-300" />
      <div className="flex space-x-4 text-xl text-gray-600 items-center">
        <FaRegEnvelope className="cursor-pointer" />
        <LuBellRing className="cursor-pointer" />
        <img
          src="https://i.pravatar.cc/40"
          alt="User Avatar"
          className="rounded-full"
        />
        <p className="text-sm font-semibold">John Marker UI</p>
      </div>
    </div>
  );
};

export default TopNav;
