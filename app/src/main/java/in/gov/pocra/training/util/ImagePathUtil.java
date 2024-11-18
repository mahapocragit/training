/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 1/1/19 1:00 PM
 */

package in.gov.pocra.training.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;


public class ImagePathUtil {


    private Context mContext;

    public ImagePathUtil(Context mContext) {
        this.mContext = mContext;
    }

    public File createImageFile( File path, String imageName) throws IOException {
        File image = null;
        if (!path.exists()) {
            path.mkdirs();
        }

        if (path.exists()){
            image = File.createTempFile(
                    imageName,  //* prefix *//*
                    ".jpg",   //* suffix *//*
                    path     //* directory *//*
            );
        }
        return image;
    }

}
