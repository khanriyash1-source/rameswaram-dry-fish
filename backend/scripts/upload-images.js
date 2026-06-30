const cloudinary = require('cloudinary').v2;
const fs = require('fs');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '..', '.env') });

cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME || 'dwua2wijv',
  api_key: process.env.CLOUDINARY_API_KEY || '661165925595652',
  api_secret: process.env.CLOUDINARY_API_SECRET,
});

const assetsImagesDir = path.join(__dirname, '..', '..', 'android', 'app', 'src', 'main', 'assets', 'images');
const outputPath = path.join(__dirname, '..', 'image-map.json');

async function main() {
  if (!fs.existsSync(assetsImagesDir)) {
    console.error('Assets images directory not found at:', assetsImagesDir);
    process.exit(1);
  }

  const files = fs.readdirSync(assetsImagesDir).filter(f => /\.(jpg|jpeg|png|webp)$/i.test(f));
  console.log(`Found ${files.length} images to upload...`);

  const map = {};

  for (const file of files) {
    const filePath = path.join(assetsImagesDir, file);
    console.log(`Uploading ${file}...`);
    try {
      const result = await cloudinary.uploader.upload(filePath, {
        folder: 'rameswaram-dry-fish',
        public_id: path.parse(file).name,
        use_filename: true,
        unique_filename: false,
      });
      map[file] = result.secure_url;
      console.log(`  OK: ${result.secure_url}`);
    } catch (err) {
      console.error(`  FAIL: ${err.message}`);
    }
  }

  fs.writeFileSync(outputPath, JSON.stringify(map, null, 2));
  console.log(`\nImage map written to ${outputPath}`);
  console.log(`Total uploaded: ${Object.keys(map).length}/${files.length}`);
}

main().catch(console.error);
