package test.appcodin.com.textinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.regex.Pattern;


public class TextInput extends LinearLayout{

    private static final String PHONE_MASK_REGEX = "(\\d{3}-){1,2}\\d{4}";
    private static final String TAG = TextInput.class.getSimpleName();
    private int maxLength;
    private int minLength;
    private boolean clearIcon;
    private boolean maskValue;
    private boolean allowTypeCopyPast;
    private boolean enable;
    private String header;
    private String placeHolder;
    private String defaultValue;
    private int caseSensivity;
    private int regexValue;
    private EditText mEditText;
    public enum  СaseSensivity{
        UPPER_CASE(0),
        LOWER_CASE(1),
        NONE(2);

        private int value;
        СaseSensivity(int value) {
            this.value = value;
        }

        public int getIntValue() {
            return value;
        }
    }

    private IEventListener eventListener;

    public interface IEventListener {
        void onMaxLength();
        void oTextChange(String enteredText);
    }

    private String getRegexStringValue(int regexCode){
        switch (regexCode){
            case 0: return "(([İIŞĞÜÇÖ]))";
            case 1: return "(([iışğüçö]))";
            case 2: return "(([ışğüçöİŞĞÜÇÖI]))";
            case 3: return "^(?!\\s*$)^([\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4})?$";
            case 4: return "^[0-9a-zA-Z\\.\\-_@]*$";
            case 5: return "^([0-9]*)$";
            case 6: return "^([0-9]*[0-9\\s]*)$";
            case 7: return "^(0|[1-9][0-9]*)$";
            case 8: return "^[a-zA-ZiışğüçöİIŞĞÜÇÖ0-9]*$";
            case 9: return "^(?!\\s$)[a-zA-ZiışğüçöİIŞĞÜÇÖ0-9\\s]*$";
            case 10: return "^[a-zA-ZiışğüçöİIŞĞÜÇÖ]*$";
            case 11: return "^[a-zA-Z0-9]*$";
            case 12: return "^[a-zA-Z]*$";
            case 13: return "^(([0-9]{4})\\s){3}([0-9]{4})$";
            case 14: return "^37([0-9]{2}\\s)([0-9]{6}\\s)([0-9]{5})$";
            case 15: return "^5([0-9]{2}\\s)([0-9]{3}\\s)([0-9]{2}\\s)([0-9]{2})$";
            case 16: return "^(([2-5]|8){1})([0-9]{2}\\s)([0-9]{3}\\s";
            case 17: return "^[a-zA-ZiışğüçöİIŞĞÜÇÖ0-9]*$";
            default: return null;
        }
    }




    private TextInputLayout mTextInputLayout;


    public TextInput(Context context) {
        super(context);
    }

    public TextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_input_layout, this, true);
        LinearLayout container = (LinearLayout) getChildAt(0);
        mTextInputLayout = (TextInputLayout) container.findViewById(R.id.layout_floatingedittext_textinputlayout);
        mEditText = (EditText) container.findViewById(R.id.description_edit_test);
        mTextInputLayout.setHint(header);
        setUpEditText();
    }



    public void setEventListener(IEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Setting up mask property. If mask setted up, then regex disabled
     */
    private void setMaskValue(){
        if(maskValue){
            regexValue = -99;
            mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            caseSensivity = СaseSensivity.NONE.getIntValue();
            mEditText.addTextChangedListener(new MyPhoneTextWatcher());

        }
    }

    /**
     * Setting up MaxLength and CaseSensivity  properties
     */
    private void setLengthAndCaseFilters(){
        InputFilter lengthFilter = new TextInputFilter.LengthFilter(maxLength, new TextInputFilter.MaxLengthExceededCallBack() {
            @Override
            public void onMaxLengthExceeded() {
                if(eventListener != null){
                    eventListener.onMaxLength();
                }
            }
        });
        int filtersCount = 1;
        InputFilter capsFilter = null;
        if(caseSensivity == СaseSensivity.UPPER_CASE.getIntValue()){
            capsFilter = new TextInputFilter.AllCaps();
        }else if(caseSensivity == СaseSensivity.LOWER_CASE.getIntValue()){
            capsFilter = new TextInputFilter.AllLowerCase();
        }
        filtersCount = capsFilter != null? (filtersCount+1) : filtersCount;
        InputFilter[] filter = new InputFilter[filtersCount];
        if(filtersCount == 1){
            filter[0] = lengthFilter;
        }else if(filtersCount == 2){
            filter[0] = lengthFilter;
            filter[1] = capsFilter;
        }

        mEditText.setFilters(filter);
    }

    /**
    *Setting up of all properties of EditText
     */
    private void setUpEditText(){
        mEditText.setHint(placeHolder);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus && header != null && !header.isEmpty()){
                    mEditText.setHint("");
                }else {
                    mEditText.setHint(placeHolder);
                }
            }
        });
        setMaskValue();
        setLengthAndCaseFilters();
        setUpRegex(getRegexStringValue(regexValue));
        mEditText.setText(defaultValue);
        if (maskValue && !Pattern.matches(PHONE_MASK_REGEX, mEditText.getText())) {
            clearText();
        }


        setIsAllowCopyPaste(allowTypeCopyPast);
        setUpClearIcon();
        mEditText.setEnabled(enable);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextInput.this.eventListener != null){
                    TextInput.this.eventListener.oTextChange(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
    Setting up clear icon and action clear() on it
     */
    private void setUpClearIcon(){
        if(clearIcon){
            mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
            mEditText.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (mEditText.getRight() - mEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            clearText();

                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Setting up RegexValue property for filtering input data
     * @param regexValue
     */
    private void setUpRegex(final String regexValue){

        if(regexValue != null){
            mEditText.addTextChangedListener(new TextWatcher() {

                final Pattern sPattern
                        = Pattern.compile(regexValue);

                private boolean isValid(CharSequence s) {
                    boolean isValid = sPattern.matcher(s).matches();
                    Log.d(TAG, "regexValue->"+regexValue+", isValid->"+isValid);
                    return isValid;
                }
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    int length = text.length();

                    if(length > 0 && !isValid(text)) {
                        editable.delete(length - 1, length);
                    }
                }
            });
        }
    }

    /**
     * @param allowTypeCopyPast - copy and past enabled if true
     */
    private void setIsAllowCopyPaste(final boolean allowTypeCopyPast){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    return allowTypeCopyPast;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            });
        }
        mEditText.setLongClickable(allowTypeCopyPast);
    }

    /**
     * Sets custom attributes from XML file.
     *
     * @param context the context
     * @param attrs   the attributes defined in the XML file
     */
    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray attributeValuesArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextInput, 0, 0);
        try {
            header = attributeValuesArray.getString(R.styleable.TextInput_header);
            placeHolder = attributeValuesArray.getString(R.styleable.TextInput_placeHolder);
            defaultValue = attributeValuesArray.getString(R.styleable.TextInput_defaultValue);
            regexValue = attributeValuesArray.getInt(R.styleable.TextInput_regexValue, -99);
            caseSensivity = attributeValuesArray.getInteger(R.styleable.TextInput_caseSensivity, 2); //defaultValue NONE
            minLength = attributeValuesArray.getInteger(R.styleable.TextInput_minLength, 0);
            maxLength = attributeValuesArray.getInteger(R.styleable.TextInput_maxLength, Integer.MAX_VALUE);
            clearIcon = attributeValuesArray.getBoolean(R.styleable.TextInput_clearIcon, true);
            maskValue = attributeValuesArray.getBoolean(R.styleable.TextInput_maskValue, true);
            allowTypeCopyPast = attributeValuesArray.getBoolean(R.styleable.TextInput_allowTypeCopyPast, true);
            enable = attributeValuesArray.getBoolean(R.styleable.TextInput_enable, true);
        } finally {
            attributeValuesArray.recycle();
        }
    }


    /**
     * Returns the pos of the entered text.
     * @return text from field
     */
    public String getText(){
        return mEditText.getText().toString();
    }


    /**
     * Clears the entered pos.
     */
    public void clearText(){
        mEditText.setText("");
    }

    /**
     * Sets error message.
     * @param errorMessage
     */
    public void showError(String errorMessage){
        mEditText.setError(errorMessage);
    }

    /**
     * Clear error message
     */
    public void clearError(){
        mEditText.setError(null);
    }

    /**
     * Set focus in the field.
     */
    public void focus(){
        mEditText.requestFocus();
    }

    private int textlength = 0;

    public class MyPhoneTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            String text = mEditText.getText().toString();
            textlength = mEditText.getText().length();

            if (text.endsWith(" "))
                return;

            if (textlength == 1) {
                if (!text.contains("(")) {
                    mEditText.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                    mEditText.setSelection(mEditText.getText().length());
                }

            } else if (textlength == 5) {

                if (!text.contains(")")) {
                    mEditText.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                    mEditText.setSelection(mEditText.getText().length());
                }

            } else if (textlength == 6 || textlength == 10) {
                mEditText.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                mEditText.setSelection(mEditText.getText().length());
            }

        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
