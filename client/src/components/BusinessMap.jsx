import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { useEffect, useRef } from "react";

export function BusinessMap({ address, coordinates }) {
  const mapElementRef = useRef(null);
  const mapInstanceRef = useRef(null);

  useEffect(() => {
    if (!mapElementRef.current || mapInstanceRef.current) return undefined;

    const map = L.map(mapElementRef.current, {
      scrollWheelZoom: false,
      zoomControl: true,
    }).setView(coordinates, 16);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    const marker = L.divIcon({
      className: "kanin-map-marker",
      html: "<span>K</span>",
      iconSize: [42, 42],
      iconAnchor: [21, 42],
      popupAnchor: [0, -38],
    });

    L.marker(coordinates, { icon: marker })
      .addTo(map)
      .bindPopup(`<strong>Kanin NYC</strong><br>${address}`);

    mapInstanceRef.current = map;

    return () => {
      map.remove();
      mapInstanceRef.current = null;
    };
  }, []);

  return (
    <div className="location-map-wrap" aria-label={`Map showing Kanin NYC at ${address}`}>
      <div className="location-map" ref={mapElementRef} />
    </div>
  );
}
