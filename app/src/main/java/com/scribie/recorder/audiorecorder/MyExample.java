package com.scribie.recorder.audiorecorder;

/**
 * Created by yukti on 9/8/16.
 */
public class MyExample {

    public static void main(String args[]) {
        int a[][] = {{1, 1, 1}, {0, 0, 1}};
        int count = pathTraversed(a);
    }

    public static int pathTraversed(int a[][])

    {

        int count = 0;
        int m, n;

        int i = 0, j = 0;
        n = a.length;

        for (i = 0; i < a.length; i++) {
            m = a[n].length;
            if (i == n && j == m)

                count++;//found one path

            for (j = 1; j <= m; j++) {

                if (a[i][j] != 0 && i < n && j < m)

                    continue;

                j = j - 1;//go back to the previous column and go down

                i = i + 1;//go to the next row

                if (a[i][j] != 0 && i < n && j < m) {

                    continue;

//no path for j


                }

            }



        }
        return count;
    }
}
