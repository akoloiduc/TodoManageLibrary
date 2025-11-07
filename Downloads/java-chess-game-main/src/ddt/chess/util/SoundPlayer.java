    package ddt.chess.util;

    import javax.sound.sampled.*;
    import java.io.IOException;
    import java.net.URL;

    public class SoundPlayer {
        private Clip moveClip;
        private Clip captureClip;
        private Clip checkClip;
        private Clip castlingClip;
        private Clip promotionClip;
        private Clip invalidMoveClip;
        private Clip gameOverClip;

        public SoundPlayer() {
            try {
                // Tải các file âm thanh đã có
                loadClip("/sound/move.wav", clip -> moveClip = clip);
                loadClip("/sound/capture.wav", clip -> captureClip = clip);
                loadClip("/sound/move-check.wav", clip -> checkClip = clip);

                // Tải các file âm thanh mới
                loadClip("/sound/castle.wav", clip -> castlingClip = clip);
                loadClip("/sound/promote.wav", clip -> promotionClip = clip);
                loadClip("/sound/game-end.wav", clip -> gameOverClip = clip);

            } catch (Exception e) {
                System.err.println("Không thể tải file âm thanh: " + e.getMessage());
            }
        }

        private void loadClip(String path, ClipConsumer consumer) {
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    System.out.println("Found sound file: " + path + " at " + url.toString());
                    AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    consumer.accept(clip);
                } else {
                    System.err.println("Sound file not found: " + path);
                    // Thử tìm với đường dẫn tuyệt đối
                    URL absoluteUrl = getClass().getResource(path.startsWith("/") ? path : "/" + path);
                    if (absoluteUrl != null) {
                        System.out.println("Found with absolute path: " + absoluteUrl.toString());
                    } else {
                        System.err.println("Also not found with absolute path");
                    }
                }
            } catch (Exception e) {
                System.err.println("Không thể tải file âm thanh " + path + ": " + e.getMessage());
                e.printStackTrace(); // In ra stack trace để debug
            }
        }

        // Interface hỗ trợ cho phương thức loadClip
        @FunctionalInterface
        private interface ClipConsumer {
            void accept(Clip clip) throws LineUnavailableException;
        }

        private void playSound(Clip clip) {
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }
        public void playCheckSound() {
            System.out.println("Playing check sound...");
            if (checkClip == null) {
                System.out.println("Warning: Check sound clip is null!");
                // Thử tải lại âm thanh
                try {
                    loadClip("/sound/move-check.wav", clip -> checkClip = clip);
                } catch (Exception e) {
                    System.err.println("Failed to reload check sound: " + e.getMessage());
                }
            }
            playSound(checkClip);
        }

        public void playMoveSound() {
            playSound(moveClip);
        }

        public void playCaptureSound() {
            playSound(captureClip);
        }

        public void playCastlingSound() {
            playSound(castlingClip);
        }

        public void playPromotionSound() {
            playSound(promotionClip);
        }

        public void playGameOverSound() {
            playSound(gameOverClip);
        }
    }