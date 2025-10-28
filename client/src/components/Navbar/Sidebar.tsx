import { RxDashboard } from "react-icons/rx";
import { GrBarChart } from "react-icons/gr";
import { FaRegBookmark } from "react-icons/fa";
import { BiWallet } from "react-icons/bi";
import { BsPeople } from "react-icons/bs";
import { GoPerson } from "react-icons/go";
import { HiOutlineEnvelope } from "react-icons/hi2";
import { LuSquareArrowOutUpLeft } from "react-icons/lu";
import NavButton from "./NavButton";
import logo from "../../assets/logo.svg";
import { useState } from "react";

const Sidebar = () => {
  const [activeNav, setActiveNav] = useState("Dashboard");

  const navItems = [
    { label: "Dashboard", icon: <RxDashboard size={13} /> },
    { label: "Stock", icon: <GrBarChart size={13} /> },
    { label: "Favorit", icon: <FaRegBookmark size={13} /> },
    { label: "Wallet", icon: <BiWallet size={13} /> },
  ];

  const otherNavitems = [
    { label: "Our community", icon: <BsPeople size={13} /> },
    { label: "Profile", icon: <GoPerson size={13} /> },
    { label: "Contact Us", icon: <HiOutlineEnvelope size={13} /> },
    { label: "Logout", icon: <LuSquareArrowOutUpLeft size={13} /> },
  ];

  return (
    <div className="w-54 h-screen bg-white shadow-md flex flex-col items-center p-4 border-b">
      <div className="w-40 ">
        <div className="flex items-center w-40 mb-8">
          <img src={logo} alt="Logo" className="h-5 w-5 object-contain" />
          <p className="font-bold text-lg px-2">Stocksty</p>
        </div>

        <div className="flex flex-col gap-2 cursor-pointer">
          {navItems.map((item) => (
            <NavButton
              key={item.label}
              active={activeNav == item.label}
              label={item.label}
              icon={item.icon}
              onClick={() => setActiveNav(item.label)}
            />
          ))}
        </div>

        <p className="my-3 text-gray-400">Account</p>

        <div className="flex flex-col gap-2 cursor-pointer">
          {otherNavitems.map((item) => (
            <NavButton
              key={item.label}
              active={activeNav == item.label}
              label={item.label}
              icon={item.icon}
              onClick={() => setActiveNav(item.label)}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
