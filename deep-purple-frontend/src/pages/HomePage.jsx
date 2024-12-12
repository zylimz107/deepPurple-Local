import { useNavigate } from "react-router-dom";
import LoginRegister from "@/components/LoginPage/Login";

export default function HomePage() {
  const navigate = useNavigate();

  // Redirect based on user role
  const handleRedirect = (role) => {
    if (role === "admin") {
      navigate("/admin/dashboard");
    } else if (role === "user") {
      navigate("/user/dashboard");
    }
  };

  return (
    <div>
      <h1>Welcome to DeepPurple</h1>
      <LoginRegister onRedirect={handleRedirect} />
    </div>
  );
}
