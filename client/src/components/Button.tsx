interface ButtonProps {
  value: string;
  onClick?: () => void;
  type?: "button" | "submit" | "reset";
  disabled?: boolean;
  variant?: "primary" | "secondary" | "outline" | "text";
  size?: "sm" | "md" | "lg";
  className?: string;
  startIcon?: React.ReactNode;
  endIcon?: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({
  value,
  onClick,
  type = "button",
  disabled = false,
  variant = "primary",
  size = "md",
  className = "",
  startIcon,
  endIcon,
}) => {
  const baseClasses =
    "inline-flex items-center justify-center font-medium rounded focus:outline-none focus:ring-2 focus:ring-offset-1 transition-colors";
  const sizeClasses = {
    sm: "px-3 py-2 text-sm",
    md: "px-4 py-2 text-base",
    lg: "px-6 py-3 text-lg",
  };

  const variantClasses = {
    primary:
      "w-full bg-[#8044FE] text-white font-bold py-2 px-4 mt-4 rounded hover:bg-purple-700 transition-colors rounded-2xl",
    secondary:
      "w-full bg-gray-600 text-white hover:bg-gray-700 focus:ring-gray-500",
    outline:
      "w-full bg-transparent border border-gray-400 text-gray-600 hover:bg-gray-100 focus:ring-gray-100",
    text: "bg-transparent text-gray-600 hover:bg-gray-100 focus:ring-gray-500",
  };

  const disabledClasses = disabled ? "opacity-50 cursor-not-allowed" : "";

  const buttonClasses = `
        ${baseClasses} 
        ${sizeClasses[size]} 
        ${variantClasses[variant]} 
        ${className} 
        ${disabledClasses}
    `
    .trim()
    .replace(/\s+/g, " ");

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={buttonClasses}
    >
      {startIcon && <span className="mr-2 flex items-center">{startIcon}</span>}
      {value}
      {endIcon && <span className="ml-2">{endIcon}</span>}
    </button>
  );
};

export default Button;
