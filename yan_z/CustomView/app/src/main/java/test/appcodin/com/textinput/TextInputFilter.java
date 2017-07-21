package test.appcodin.com.textinput;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

public interface TextInputFilter extends InputFilter {

    public interface MaxLengthExceededCallBack {
        void onMaxLengthExceeded();
    }

    /**
     * This filter will set to lower case all the caps case letters that are added
     * through edits.
     */
    public static class AllLowerCase implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isUpperCase(source.charAt(i))) {
                    char[] v = new char[end - start];
                    TextUtils.getChars(source, start, end, v, 0);
                    String s = new String(v).toLowerCase();

                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(s);
                        TextUtils.copySpansFrom((Spanned) source,
                                start, end, null, sp, 0);
                        return sp;
                    } else {
                        return s;
                    }
                }
            }

            return null; // keep original
        }
    }

    /**
     * This filter will care about max length and inform about exceeding through callback
     */
    public class LengthFilter extends InputFilter.LengthFilter {
        private MaxLengthExceededCallBack mCallBack;
        public LengthFilter(int max, MaxLengthExceededCallBack callBack) {
            super(max);
            mCallBack = callBack;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence res = super.filter(source, start, end, dest, dstart, dend);
            if(mCallBack != null && res != null){
                mCallBack.onMaxLengthExceeded();
            }
            return res;
        }
    }
}
