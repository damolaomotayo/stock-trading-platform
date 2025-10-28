interface NavButtonProps {
  active: boolean;
  label: string;
  icon: React.ReactNode;
  onClick: () => void;
}

const NavButton: React.FC<NavButtonProps> = ({
  active,
  label,
  icon,
  onClick,
}) => {
  return (
    <div
      className={`flex items-center justify-start gap-2 px-4 py-1 w-full rounded-md ${
        active ? "bg-gray-100" : ""
      }`}
      onClick={onClick}
    >
      <div className="text-gray-500">{icon}</div>
      <span className="text-black-600 font-semibold text-sm">{label}</span>
    </div>
  );
};

export default NavButton;
