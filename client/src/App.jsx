import { Hero } from "./components/Hero.jsx";
import { InstagramFeed } from "./components/InstagramFeed.jsx";
import { LocationSection } from "./components/LocationSection.jsx";
import { MenuSection } from "./components/MenuSection.jsx";
import { Mission } from "./components/Mission.jsx";

function App() {
  return (
    <>
      <Hero />
      <Mission />
      <MenuSection />
      <InstagramFeed />
      <LocationSection />
    </>
  );
}

export default App;
