import chickenAdoboImage from "../assets/menu/chicken-adobo.jpg";
import haloHaloImage from "../assets/menu/halo-halo.jpg";
import lumpiaImage from "../assets/menu/lumpia.jpg";
import palabokImage from "../assets/menu/palabok.jpg";
import sagoGulamanImage from "../assets/menu/sago-gulaman.jpg";
import ubeCoolerImage from "../assets/menu/ube-cooler.jpg";
import { ImageSlot } from "./ImageSlot.jsx";
import { SectionLabel } from "./SectionLabel.jsx";

const menuItems = [
  {
    title: "Chicken Adobo",
    description: "Halal Boneless Chicken Thighs Braised in Soy Sauce, Coconut Milk, Vinegar, Garlic",
    price: "$6",
    image: chickenAdoboImage,
    imageAlt: "Chicken Adobo",
  },
  {
    title: "Lumpia",
    description: "Fried Pork Spring Rolls: Pork, Scallions, Carrots, Jicama, Mushrooms with Sweet Chilli Dipping Sauce",
    price: "$6",
    image: lumpiaImage,
    imageAlt: "Lumpia",
  },
  {
    title: "Palabok",
    description: "Rice Noodles with Shrimp Bisque and Garnished with Pork Chicharon, Shrimp, Egg, Fried Garlic, Scallions",
    price: "$6",
    image: palabokImage,
    imageAlt: "Palabok",
  },
  {
    title: "Halo Halo",
    description: "Meaning 'Mix Mix' in Tagalog, Halo Halo is a Layered Shaved Ice Dessert with Sweet Red Beans, Jellies, Toasted Rice Pinipig, and Ube Halaya",
    price: "$6",
    image: haloHaloImage,
    imageAlt: "Halo Halo",
  },
  {
    title: "Sago't Gulaman",
    description: "Brown Sugar Sahved Ice Drink with Tapioca Pearls and Agar Jelly",
    price: "$6",
    image: sagoGulamanImage,
    imageAlt: "Sago't Gulaman",
  },
  {
    title: "Ube Cooler",
    description: "Creamy Purple Yam Cooler Ice Drink with Tapioca Pearls",
    price: "$6",
    image: ubeCoolerImage,
    imageAlt: "Ube Cooler",
  },
];

function MenuCard({ item }) {
  return (
    <article className="menu-item">
      <ImageSlot
        src={item.image}
        alt={item.imageAlt}
        label={item.title}
        className="menu-placeholder"
      />
      <div>
        <h3>{item.title}</h3>
        <p>{item.description}</p>
        <span>{item.price}</span>
      </div>
    </article>
  );
}

export function MenuSection() {
  return (
    <section className="menu-section" id="menu">
      <div className="section-heading">
        <SectionLabel>Menu</SectionLabel>
        <h2>Current Favorites!</h2>
      </div>

      <div className="menu-list">
        {menuItems.map((item) => (
          <MenuCard item={item} key={item.title} />
        ))}
      </div>
    </section>
  );
}
