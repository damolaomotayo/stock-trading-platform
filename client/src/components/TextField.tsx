import { forwardRef } from "react";

export interface TextFieldProps {
  label: string;
  type?: "text" | "password" | "email" | "number";
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  required?: boolean;
  className?: string;
  disabled?: boolean;
  error?: string;
  helperText?: string;
  autoComplete?: string;
}

const TextField = forwardRef<HTMLInputElement, TextFieldProps>(
  (
    {
      label,
      type = "text",
      value,
      onChange,
      placeholder,
      required = false,
      disabled = false,
      error,
      helperText,
      autoComplete,
      className = "",
    },
    ref
  ) => {
    const inputId = `text-field-${label.replace(/\s+/g, "-").toLowerCase()}`;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      onChange(e.target.value);
    };

    return (
      <div className={`mb-4 ${className}`}>
        <label
          htmlFor={inputId}
          className={`block text-sm font-medium mb-2 ${
            error ? "text-red-600" : "text-gray-700"
          }`}
        >
          {label}
          {required && <span className="text-red-500">*</span>}
        </label>
        <input
          id={inputId}
          ref={ref}
          type={type}
          value={value}
          onChange={handleChange}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          autoComplete={autoComplete}
          className={`
                        w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-1 transition-colors
                        ${
                          error
                            ? "border-red-500 focus-ring-red-500 focus:border-red-500"
                            : "border-gray-300 focus:ring-blue-500 focus:border-blue-500"
                        }  
                        ${
                          disabled
                            ? "bg-gray-100 cursor-not-allowed"
                            : "bg-white"
                        } 
                        `}
        />
        {(error || helperText) && (
          <p
            className={`mt-1 text-sm ${
              error ? "text-red-600" : "text-gray-500"
            }`}
          >
            {" "}
            {error || helperText}
          </p>
        )}
      </div>
    );
  }
);

TextField.displayName = "TextField";

export default TextField;
