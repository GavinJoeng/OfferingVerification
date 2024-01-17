package com.chinamobile.constant;

/**
 * @description: 用於存儲Matrix模式的路經
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/15 16:32
 */
public class MatrixPatternPath {

    //MATRIX_ID
    public static String MATRIX_ID = "1696989206252108387";
    //MATRIX_ID數組
    //public static String[] MATRIX_ID_LIST = {"1696989206252108388","1696989206252108387"};
    //存儲和讀取文件夾基本路經
    public static String BASIC_PATH = "C:\\Users\\P7587\\Desktop\\testFile\\";
    //從華為數據庫複製的信息放在TXT文件夾中
    public static String MATRIX_TXT_FILE = "_MATRIX_PATTERN_RULE.txt";
    //xml文件存儲名稱
    public static String MATRIX_XML_FILE = "_MATRIX_PATTERN_RULE.xml";
    //xml文件轉碼後的<code>標籤存儲名稱
    public static String MATRIX_XML_FILER_FILE = "_MATRIX_PATTERN_FILTER.xml";
    //JSON文件存儲名稱
    public static String MATRIX_JSON_PATH = "_MATRIX_PATTERN_RULE.json";

    //這是樹狀圖的開頭基礎路經，格式基本固定。
    //結尾需要加上[數組下標]
    public static String BASIC_NODE_PATH_START = "$.[\"pattern-action-union\"].pattern[\"condition-selection-pattern\"][\"condition-node\"]";

    //結尾需要加上[數組下標]
    //如果判斷為非葉子節點則進行增加該路經
    //不加下標則是獲取conditionNodeList，可獲取數組大小和數組數據
    //這是樹狀圖的中間路經，格式基本固定。
    public static String BASIC_NODE_PATH_MID = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"condition-selection-pattern\"][\"condition-node\"]";
    //判斷是否是矩陣模式
    public static String IS_MATRIX_PATTERN_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"@attributes\"][\"chosen-element\"]";

    public static String MATRIX_ACTION_TYPE_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"matrix-selection-pattern\"][\"col-def\"][\"action-def\"][\"action-default-values\"][\"pattern-action-union\"].action[\"@attributes\"][\"chosen-element\"]";

    public static String MATRIX_ID_PATH = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"matrix-selection-pattern\"][\"general-matrix-info\"][\"matrix-id\"].value";

    //<!-- data-element="C_CALLING_HOME_CC" -->
    public static String MATRIX_DATA_ELEMENT = "[\"sub-pattern-and-action\"][\"pattern-action-union\"].pattern[\"matrix-selection-pattern\"][\"col-def\"][\"condition-col-def\"][\"@attributes\"][\"data-element\"]";

    //以下參數必須讀取到數據庫的Matrix的xml才可以進行讀取。
    //Free Unit Type    Local GPRS Bucket C
    public static String FREE_UNIT_TYPE_ID_PATH = "$.[\"pattern-action-union\"].action[\"free-unit-bonus-action\"][\"free-unit-type-id\"].value";

    //系統是以bits進行存儲，該程序是讀取display屬性來進行存儲
    //Bonus Amount  32212254720
    public static String MATRIX_BONUS_AMOUNT_PATH = "$.[\"pattern-action-union\"].action[\"free-unit-bonus-action\"][\"bonus-amount\"].constant[\"constant-value\"][\"@attributes\"][\"display-value\"]";

    //Measurement Unit  KB
    public static String MATRIX_MEASUREMENT_ID_PATH = "$.[\"pattern-action-union\"].action[\"free-unit-bonus-action\"][\"display-measurement-id\"].value";
}
