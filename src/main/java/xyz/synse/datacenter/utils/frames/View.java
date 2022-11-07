package xyz.synse.datacenter.utils.frames;

public enum View {
    MAIN_VIEW,
    RAW_IMAGE,
    MOTION_PROCESSED;

    public static View next(View view){
        if (view == View.MAIN_VIEW) {
            return View.RAW_IMAGE;
        } else if (view == View.RAW_IMAGE) {
            return View.MOTION_PROCESSED;
        } else if (view == View.MOTION_PROCESSED) {
            return View.MAIN_VIEW;
        }

        return View.MAIN_VIEW;
    }
}
