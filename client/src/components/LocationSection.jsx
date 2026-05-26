import { BusinessMap } from "./BusinessMap.jsx";
import { SectionLabel } from "./SectionLabel.jsx";

const location = {
  title: "Find us at Queens Night Market in Flushing Corona Park",
  address: "47-01 111th St, Corona, NY 11368",
  hours: "Every Saturday, 5 PM to 12 AM",
  mapUrl: "https://maps.google.com/?q=47-01+111th+St+Corona+NY+11368",
  coordinates: [40.747258, -73.851693],
};

export function LocationSection() {
  return (
    <section className="location-section" id="location">
      <div className="location-copy">
        <SectionLabel>Location</SectionLabel>
        <h2>{location.title}</h2>
        <p>{location.address}</p>
        <p>{location.hours}</p>
        <a
          className="button primary"
          href={location.mapUrl}
          target="_blank"
          rel="noreferrer"
        >
          Get directions
        </a>
      </div>
      <BusinessMap address={location.address} coordinates={location.coordinates} />
    </section>
  );
}
