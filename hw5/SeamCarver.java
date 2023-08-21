import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    // 这个homework一定要先处理好矩阵再做，否则很容易陷入混乱。
    // 2023.8.20于文图
    private Picture picture;
    private int width;
    private int height;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException();
        }
        int[] rgbLeft;
        int[] rgbRight;
        int[] rgbUp;
        int[] rgbDown;

        rgbRight = (x < width - 1 ? getRGBArr(x + 1, y) : getRGBArr(0, y));
        rgbLeft = (x > 0 ? getRGBArr(x - 1, y) : getRGBArr(width - 1, y));
        rgbUp = (y < height - 1 ? getRGBArr(x, y + 1) : getRGBArr(x, 0));
        rgbDown = (y > 0 ? getRGBArr(x, y - 1) : getRGBArr(x, height - 1));

        double Rx = Math.pow(rgbRight[0] - rgbLeft[0], 2);
        double Gx = Math.pow(rgbRight[1] - rgbLeft[1], 2);
        double Bx = Math.pow(rgbRight[2] - rgbLeft[2], 2);
        double Ry = Math.pow(rgbUp[0] - rgbDown[0], 2);
        double Gy = Math.pow(rgbUp[1] - rgbDown[1], 2);
        double By = Math.pow(rgbUp[2] - rgbDown[2], 2);
        return Rx + Ry + Gx + Gy + Bx + By;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] e = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                e[j][i] = energy(i, j);
            }
        }

        double[][] minEnergy = new double[height][width];
        int[][] minPath = new int[height][width];
        for (int i = 0; i < height; i++) {
            minEnergy[i][0] = e[i][0];
            minPath[i][0] = -1;
        }

        // 这里应该用自己熟悉的寻址方式写
        // 这里的循环顺序很有讲究
        for (int j = 1; j < width; j++) {
            for (int i = 0; i < height; i++) {
                if (i == 0) {
                    minEnergy[i][j] = e[i][j] + Math.min(minEnergy[i][j - 1], minEnergy[i + 1][j - 1]);
                    minPath[i][j] = (minEnergy[i][j - 1] < minEnergy[i + 1][j - 1]? i : i + 1);
                } else if (i == height - 1) {
                    minEnergy[i][j] = e[i][j] + Math.min(minEnergy[i][j - 1], minEnergy[i - 1][j - 1]);
                    minPath[i][j] = (minEnergy[i][j - 1] < minEnergy[i - 1][j - 1]? i : i - 1);
                } else {
                    double tmp = Math.min(minEnergy[i - 1][j - 1], minEnergy[i + 1][j - 1]);
                    minPath[i][j] = (minEnergy[i - 1][j - 1] < minEnergy[i + 1][j - 1]? i - 1 : i + 1);
                    minEnergy[i][j] = e[i][j] + Math.min(tmp, minEnergy[i][j - 1]);
                    minPath[i][j] = (tmp < minEnergy[i][j - 1]? minPath[i][j] : i);
                }
            }
        }

        int flag = 0;
        double cost = minEnergy[0][width - 1];
        for (int i = 1; i < height; i++) {
            if (minEnergy[i][width - 1] < cost) {
                flag = i;
                cost = minEnergy[i][width - 1];
            }
        }
        int[] res = new int[width];
        res[width - 1] = flag;
        for (int i = width - 2; i != -1; i--) {
            flag = minPath[flag][i + 1];
            res[i] = flag;
        }
        return res;
    }

    // sequence of indices for vertical seam
    // refer to other answers to avoid copying.
    public int[] findVerticalSeam() {
        double[][] e = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                e[j][i] = energy(i, j);
            }
        }

        double[][] minEnergy = new double[height][width];
        int[][] minPath = new int[height][width];
        for (int i = 0; i < width; i++) {
            minEnergy[0][i] = e[0][i];
            minPath[0][i] = -1;
        }

        // 这里应该用自己熟悉的寻址方式写
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j == 0) {
                    minEnergy[i][j] = e[i][j] + Math.min(minEnergy[i - 1][j], minEnergy[i - 1][j + 1]);
                    minPath[i][j] = (minEnergy[i - 1][j] < minEnergy[i - 1][j + 1]? j : j + 1);
                } else if (j == width - 1) {
                    minEnergy[i][j] = e[i][j] + Math.min(minEnergy[i - 1][j - 1], minEnergy[i - 1][j]);
                    minPath[i][j] = (minEnergy[i - 1][j - 1] < minEnergy[i - 1][j]? j - 1 : j);
                } else {
                    double tmp = Math.min(minEnergy[i - 1][j - 1], minEnergy[i - 1][j + 1]);
                    minPath[i][j] = (minEnergy[i - 1][j + 1] < minEnergy[i - 1][j - 1]? j + 1 : j - 1);
                    minEnergy[i][j] = e[i][j] + Math.min(tmp, minEnergy[i - 1][j]);
                    minPath[i][j] = (tmp < minEnergy[i - 1][j]? minPath[i][j] : j);
                }
            }
        }

        int flag = 0;
        double cost = minEnergy[height - 1][0];
        for (int i = 1; i < width; i++) {
            if (minEnergy[height - 1][i] < cost) {
                flag = i;
                cost = minEnergy[height - 1][i];
            }
        }
        int[] res = new int[height];
        res[height - 1] = flag;
        for (int i = height - 2; i != -1; i--) {
            flag = minPath[i + 1][flag];
            res[i] = flag;
        }
        return res;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
        picture = SeamRemover.removeHorizontalSeam(picture, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length != width) {
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
        picture = SeamRemover.removeVerticalSeam(picture, seam);
    }

    private int[] getRGBArr(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException();
        }
        int[] arr = new int[3];
        int rgb = picture.getRGB(x, y);
        arr[0] = (rgb >> 16) & 0xFF;
        arr[1] = (rgb >> 8) & 0xFF;
        arr[2] = (rgb) & 0xFF;
        return arr;
    }
}
