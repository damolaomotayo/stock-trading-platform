import { RiFacebookFill } from "react-icons/ri";
import { FaInstagram } from "react-icons/fa";
import { TfiLinkedin } from "react-icons/tfi";
import { FaRegEnvelope } from "react-icons/fa6";
import logo from "../../assets/footer-logo.svg";
import SearchBox from "../SearchBox/SearchBox";

const Footer = () => {
  return (
    <footer className="w-full bg-[#0A0A22] text-white flex justify-between">
      <div></div>
      {/* footer contents */}
      <div className="flex flex-col md:justify-around gap-8 pl-15 py-8">
        {/* row divs */}
        <div className="flex flex-col md:flex-row md:justify-between px-10 pr-20">
          <div className="flex flex-col gap-2 md:w-1/4">
            <div className="flex items-center space-x-4">
              <img
                src={logo}
                alt="Company Logo"
                className="h-5 w-5 object-contain"
              />
              <p className="font-bold text-lg">Stocksty</p>
            </div>
            <p className="text-sm text-gray-300">
              Ease of shoppping is our main focus. With powerful search features
              and customizable filters, you can easily fin the products you are
              waiting for.
            </p>
            <div className="flex space-x-4 mt-2 items-center text-gray-600">
              <div className="p-1 bg-[#565A65] rounded-full">
                <RiFacebookFill size={20} color="#161534" />
              </div>
              <div className="p-1 bg-[#565A65] rounded-full">
                <FaInstagram size={20} color="#161534" />
              </div>
              <div className="p-1 bg-[#565A65] rounded-full">
                <TfiLinkedin size={20} color="#161534" />
              </div>
            </div>
            <div className="mt-2 text-sm font-bold w-70 space-y-2">
              <p>Subscribe to Newsletter</p>
              <SearchBox
                className="border-[#0062FE] border-1"
                icon={<FaRegEnvelope />}
              />
            </div>
          </div>
          <div className="flex flex-col cursor-pointer gap-3">
            <h3 className="font-bold ">Get Started</h3>
            <div className="text-sm text-gray-300 flex flex-col gap-2">
              <p>Service</p>
              <p>Contact Us</p>
              <p>Affiliate Program</p>
              <p>About us</p>
            </div>
          </div>
          <div className="flex flex-col cursor-pointer gap-3">
            <h3 className="font-bold">Support</h3>
            <div className="text-sm text-gray-300 flex flex-col gap-2">
              <p>Help Center</p>
              <p>Terms of Service</p>
              <p>Legal</p>
              <p>Privacy Policy</p>
            </div>
          </div>
          <div className="flex flex-col cursor-pointer gap-3">
            <h3 className="font-bold">Support</h3>
            <div className="text-sm text-gray-300 flex flex-col gap-2">
              <p>Help Center</p>
              <p>Terms of Service</p>
              <p>Legal</p>
              <p>Privacy Policy</p>
            </div>
          </div>
        </div>
        {/* bottom line */}
        <div className="pl-8">
          <div className="mt-10 bg-gray-600 h-0.5"></div>
          <div className="flex flex-col md:flex-row justify-between px-8 mt-4 text-sm text-gray-400">
            <p>
              2025
              <em> MaxFit</em>
            </p>
            <p>Twitter - Instagram - Facebook</p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
