import { useState } from "react";
import hero from "../assets/trading.jpg";
import TextField from "../components/TextField";
import SocialButton from "../components/SocialButton";

interface LoginFormData {
  email: string;
  password: string;
}

const LoginPage = () => {
  const [formData, setFormData] = useState<LoginFormData>({
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState<Partial<LoginFormData>>({});

  const handleChange = (field: keyof LoginFormData) => (value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));

    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors: Partial<LoginFormData> = {};
    if (!formData.email) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email is invalid";
    }

    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
  };

  return (
    <div className="flex justify-between h-screen">
      <div className="w-3/4 flex flex-col items-center justify-center py-8">
        <h1 className="text-4xl font-bold">Welcome Back</h1>
        <h4 className="text-md pb-8">SignIn to your account</h4>
        <form onSubmit={handleSubmit} className="w-96">
          <TextField
            label="Email"
            type="email"
            value={formData.email}
            onChange={handleChange("email")}
            placeholder="Enter your email"
            required
            error={errors.email}
            autoComplete="email"
          />
          <TextField
            label="Password"
            type="password"
            value={formData.password}
            onChange={handleChange("password")}
            placeholder="Create a password"
            required
            error={errors.password}
            autoComplete="new-password"
          />
          <button
            type="submit"
            className="w-full bg-[#8044FE] text-white font-bold py-2 px-4 mt-4 rounded hover:bg-purple-700 transition-colors rounded-2xl"
          >
            Login
          </button>
        </form>
        <p className="pt-8 pb-4">OR</p>
        <div className="space-y-3 w-96">
          <SocialButton
            provider="google"
            onClick={(provider) => console.log(`Login with ${provider}`)}
            variant="outline"
          />
          <SocialButton
            provider="facebook"
            onClick={(provider) => console.log(`Login with ${provider}`)}
          />
        </div>
      </div>
      <div className="w-1/2">
        <img src={hero} alt="Hero" className="h-full w-full object-cover" />
      </div>
      <div></div>
    </div>
  );
};

export default LoginPage;
