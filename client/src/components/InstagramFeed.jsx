import { useEffect } from "react";
import { SectionLabel } from "./SectionLabel.jsx";

const instagramPostUrls = [
  "https://www.instagram.com/reel/C9NEGTFx2hD/",
  "https://www.instagram.com/reel/CvfYxubvgx0/",
  "https://www.instagram.com/p/CJRvzMRBhIS/",
];

export function InstagramFeed() {
  useEffect(() => {
    const existingScript = document.querySelector('script[src="https://www.instagram.com/embed.js"]');

    if (existingScript) {
      window.instgrm?.Embeds?.process();
      return;
    }

    const script = document.createElement("script");
    script.async = true;
    script.src = "https://www.instagram.com/embed.js";
    script.onload = () => window.instgrm?.Embeds?.process();
    document.body.appendChild(script);
  }, []);

  return (
    <section className="instagram-section" id="instagram">
      <div className="section-heading">
        <div>
          <SectionLabel>Instagram</SectionLabel>
          <h2>Highlights from @kanin.nyc</h2>
        </div>
      </div>

      <div className="instagram-embed-wrap">
        {instagramPostUrls.map((postUrl) => (
          <blockquote
            className="instagram-media"
            data-instgrm-permalink={`${postUrl}?utm_source=ig_embed&utm_campaign=loading`}
            data-instgrm-version="14"
            key={postUrl}
          >
            <a href={`${postUrl}?utm_source=ig_embed&utm_campaign=loading`} target="_blank" rel="noreferrer">
              View this post on Instagram
            </a>
          </blockquote>
        ))}
      </div>
    </section>
  );
}
