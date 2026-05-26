export function ImageSlot({ src, alt, label, className = "" }) {
  if (src) {
    return <img src={src} alt={alt} />;
  }

  return <div className={`image-placeholder ${className}`}>{label}</div>;
}
