#!/usr/bin/env bash
# Build Puente Gumroad cover videos (30s + 60s).
# Requires: chromium/chrome, ffmpeg, edge-tts (EDGE_TTS env or venv path)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$(cd "$ROOT/.." && pwd)"
FRAMES="$ROOT/frames"
AUDIO="$ROOT/audio"
EDGE_TTS="${EDGE_TTS:-/tmp/edge-tts-venv/bin/edge-tts}"
VOICE="en-US-AndrewNeural"
CHROME="${CHROME:-}"

if [[ -z "$CHROME" ]]; then
  for c in chromium google-chrome-stable google-chrome chromium-browser; do
    if command -v "$c" >/dev/null 2>&1; then
      CHROME="$(command -v "$c")"
      break
    fi
  done
fi
[[ -n "$CHROME" ]] || { echo "No Chrome/Chromium found"; exit 1; }
command -v ffmpeg >/dev/null || { echo "ffmpeg required"; exit 1; }
[[ -x "$EDGE_TTS" ]] || { echo "edge-tts not found at $EDGE_TTS"; exit 1; }

mkdir -p "$FRAMES" "$AUDIO"

screenshot_scene() {
  local html="$1"
  local png="$2"
  local abs="file://$(realpath "$html")"
  "$CHROME" --headless=new --disable-gpu --hide-scrollbars \
    --force-device-scale-factor=1 \
    --window-size=1920,1080 \
    --virtual-time-budget=2500 \
    --screenshot="$png" \
    "$abs" >/dev/null 2>&1
  # Chromium sometimes writes to cwd; move if needed
  if [[ ! -f "$png" && -f "$(basename "$png")" ]]; then
    mv "$(basename "$png")" "$png"
  fi
  if [[ ! -f "$png" ]]; then
    # fallback: write next to html then move
    (cd "$(dirname "$html")" && "$CHROME" --headless=new --disable-gpu --hide-scrollbars \
      --force-device-scale-factor=1 --window-size=1920,1080 --virtual-time-budget=2500 \
      --screenshot="$(basename "$png")" "file://$(realpath "$(basename "$html")")" >/dev/null 2>&1)
    mv "$(dirname "$html")/$(basename "$png")" "$png" 2>/dev/null || true
  fi
  [[ -f "$png" ]] || { echo "Screenshot failed: $html"; exit 1; }
}

echo "== Screenshots =="
for scene in 01-hook 02-problem 03-magic 04-cta-30 05-second-turn 06-transcript 07-export 08-cta-60; do
  echo "  $scene"
  screenshot_scene "$ROOT/scenes/${scene}.html" "$FRAMES/${scene}.png"
done

echo "== TTS =="
for n in 01-hook 02-problem 03-magic 04-cta-30 05-second-turn 06-transcript 07-export 08-cta-60; do
  echo "  $n"
  "$EDGE_TTS" --voice "$VOICE" \
    --file "$ROOT/narration/${n}.txt" \
    --write-media "$AUDIO/${n}.mp3"
done

pad_audio_to() {
  local src="$1"
  local dest="$2"
  local dur="$3"
  # Pad/trim narration to exact scene duration (silence pad if short)
  ffmpeg -y -i "$src" -af "apad=whole_dur=${dur}" -t "$dur" \
    -c:a libmp3lame -q:a 4 "$dest" >/dev/null 2>&1
}

make_scene_clip() {
  local id="$1"
  local dur="$2"
  local frame="$FRAMES/${id}.png"
  local audio_raw="$AUDIO/${id}.mp3"
  local audio_pad="$AUDIO/${id}-pad.mp3"
  local clip="$FRAMES/${id}-clip.mp4"
  pad_audio_to "$audio_raw" "$audio_pad" "$dur"
  ffmpeg -y -loop 1 -i "$frame" -i "$audio_pad" \
    -c:v libx264 -tune stillimage -pix_fmt yuv420p \
    -c:a aac -b:a 128k \
    -t "$dur" -r 30 -shortest \
    -movflags +faststart \
    "$clip" >/dev/null 2>&1
}

concat_clips() {
  local listfile="$1"
  local outfile="$2"
  ffmpeg -y -f concat -safe 0 -i "$listfile" \
    -c:v libx264 -pix_fmt yuv420p -c:a aac -b:a 128k \
    -movflags +faststart \
    "$outfile" >/dev/null 2>&1
}

echo "== Encode 30s =="
make_scene_clip 01-hook 4
make_scene_clip 02-problem 6
make_scene_clip 03-magic 12
make_scene_clip 04-cta-30 8

LIST30="$FRAMES/concat-30.txt"
cat > "$LIST30" <<EOF
file '$(realpath "$FRAMES/01-hook-clip.mp4")'
file '$(realpath "$FRAMES/02-problem-clip.mp4")'
file '$(realpath "$FRAMES/03-magic-clip.mp4")'
file '$(realpath "$FRAMES/04-cta-30-clip.mp4")'
EOF
concat_clips "$LIST30" "$OUT/puente-30s.mp4"

echo "== Encode 60s =="
make_scene_clip 05-second-turn 12
make_scene_clip 06-transcript 10
make_scene_clip 07-export 8
make_scene_clip 08-cta-60 8

LIST60="$FRAMES/concat-60.txt"
cat > "$LIST60" <<EOF
file '$(realpath "$FRAMES/01-hook-clip.mp4")'
file '$(realpath "$FRAMES/02-problem-clip.mp4")'
file '$(realpath "$FRAMES/03-magic-clip.mp4")'
file '$(realpath "$FRAMES/05-second-turn-clip.mp4")'
file '$(realpath "$FRAMES/06-transcript-clip.mp4")'
file '$(realpath "$FRAMES/07-export-clip.mp4")'
file '$(realpath "$FRAMES/08-cta-60-clip.mp4")'
EOF
concat_clips "$LIST60" "$OUT/puente-60s.mp4"

echo "== Cover still =="
# JPEG poster from hook frame
ffmpeg -y -i "$FRAMES/01-hook.png" -q:v 3 "$OUT/puente-cover.jpg" >/dev/null 2>&1

echo "== VTT =="
# 30s captions matching scene timing
cat > "$OUT/puente-30s.vtt" <<'EOF'
WEBVTT

00:00:00.000 --> 00:00:04.000
Never pass 2 phones across the counter again.

00:00:04.000 --> 00:00:10.000
You should not juggle two phones across the counter.

00:00:10.000 --> 00:00:22.000
One tablet. Speak. Hear the translation. Keep the transcript.

00:00:22.000 --> 00:00:30.000
Get the Android APK — $29 one-time
EOF

cat > "$OUT/puente-60s.vtt" <<'EOF'
WEBVTT

00:00:00.000 --> 00:00:04.000
Never pass 2 phones across the counter again.

00:00:04.000 --> 00:00:10.000
You should not juggle two phones across the counter.

00:00:10.000 --> 00:00:22.000
One tablet. Speak. Hear the translation. Keep the transcript.

00:00:22.000 --> 00:00:34.000
Two speakers. One device. Take turns.

00:00:34.000 --> 00:00:44.000
A bilingual log you can hand off to the next shift.

00:00:44.000 --> 00:00:52.000
Export TXT / PDF / CSV · hand off to the next shift

00:00:52.000 --> 00:01:00.000
Get the APK — $29 · Offline after setup · Wi-Fi once
EOF

echo "== Done =="
ls -lh "$OUT/puente-30s.mp4" "$OUT/puente-60s.mp4" "$OUT/puente-cover.jpg"
ffprobe -v error -show_entries format=duration -of default=nw=1:nk=1 "$OUT/puente-30s.mp4"
ffprobe -v error -show_entries format=duration -of default=nw=1:nk=1 "$OUT/puente-60s.mp4"
