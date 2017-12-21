package com.deqiang.a24;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private char[] mOprs={'+','-','*','/'};
    char[] mChars={'1','2','3','4'};
    private int mCounter=0;
    private int mSatisfied=0;

    TextView mTextview_result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextview_result = findViewById(R.id.textViewResult);
        mTextview_result.setText("");
        mTextview_result.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    public void compute(View view){
        EditText editText = findViewById(R.id.editTextNumber);

        mChars = editText.getText().toString().toCharArray();
        mTextview_result.setText("");
        mCounter = 0;
        mSatisfied = 0;

        char[] oprs=new char[3];
        permutation(mChars,0,oprs,0);
        String str = "counter="+mCounter+" mSatisfied="+mSatisfied+"\n";
        mTextview_result.append(str);
        System.out.println(str);
    }

    public class Element{
        char s;
        int v;
        public Element(char s, int v){
            this.s = s;
            this.v = v;
        }
    }
    public void myassert(boolean flag, String str){
        try {
            if(!flag) throw new Exception(str);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public boolean isOpr(char ch){
        for(int i=0;i<mOprs.length;i++)
            if(ch==mOprs[i]) return true;

        return false;
    }
    public boolean isHigher(char ch1, char ch2){
        if (((ch1=='*')||(ch1=='/')) && ((ch2=='+')||(ch2=='-')))
            return true;
        return false;
    }
    public int compute_exp(char[] exp){
        Stack<Element> stack = new Stack();
        int num=0;

        for(int i=0;i<exp.length;i++){
            if('('==exp[i]) {
                Element e=new Element('(',0);
                stack.push(e);
                continue;
            }
            else if(')'==exp[i]){
                Element e_num = stack.pop();
                Element left = stack.pop();
                if('('==left.s){
                    num = e_num.v;
                }else{
                    myassert(false,"incorrect!");
                }
            }else if(isOpr(exp[i])){
                Element e = new Element(exp[i],0);
                stack.push(e);
                continue;
            }else{
                num = new Integer(exp[i]-'0');
            }
            //num, check if needs to process further
            while(!stack.isEmpty() && isOpr(stack.peek().s)) {
                Element opr = stack.peek();
                if(((i+1)<(exp.length-1)) && isOpr(exp[i+1]) && isHigher(exp[i+1],opr.s))
                    break;

                opr = stack.pop();
                Element another = stack.pop();
                switch (opr.s) {
                    case '*':
                        num = another.v * num;
                        break;
                    case '/':
                        if(0 == num)
                            return 0;
                        if(0!= (another.v % num))
                            return 0;
                        num = another.v/num;
                        break;
                    case '-':
                        num = another.v - num;
                        break;
                    case '+':
                        num = another.v + num;
                        break;
                    default:
                        myassert(false, "incorrect");
                }
            }

            Element e = new Element('n',num);
            stack.push(e);
        }
        return num;
    }
    /*
1个括号：(AB)CD、A(BC)D、AB(CD)、(ABC)D、A(BCD)，共5种
        2个括号：(AB)(CD)、((AB)C)D、(A(BC))D、A(B(CD))、A((BC)D)，共5种
        */
    public void listAll(char[] chars, char[] oprs){
        String[] str = {"(A*B)*C*D",
                        "A*(B*C)*D",
                        "A*B*(C*D)",
                        "(A*B*C)*D",
                        "A*(B*C*D)",
                        "(A*B)*(C*D)",
                "((A*B)*C)*D",
                "(A*(B*C))*D",
                "A*(B*(C*D))",
                "A*((B*C)*D)"};

        for(int i=0;i<str.length;i++){
            char[] exp = str[i].toCharArray();
            int opr_index=0;
            for(int j=0;j<exp.length;j++){
                if('A'==exp[j]) exp[j]=chars[0];
                else if('B'==exp[j]) exp[j]=chars[1];
                else if('C'==exp[j]) exp[j]=chars[2];
                else if('D'==exp[j]) exp[j]=chars[3];
                else if('*'==exp[j]) exp[j]=oprs[opr_index++];
            }
            int result = compute_exp(exp);
            if(24 == result){
                mSatisfied++;
                String str1 = String.format("%s=%d\n", new String(exp), result);
                mTextview_result.append(str1);
                System.out.println(str1);
            }
        }

    }
    public void permutation(char[] chars,int clevel, char[] oprs, int  olevel){
        int csize=chars.length;
        int osize=oprs==null?0:oprs.length;

        if(clevel==(csize-1)){
            if(osize == olevel){
                listAll(chars,oprs);
                //System.out.println(String.format("%s %s",new String(chars),oprs==null?"":new String(oprs)));
                mCounter++;
                return;
            }else{
                for(int i=0;i<mOprs.length;i++){
                    oprs[olevel]=mOprs[i];
                    permutation(chars,clevel,oprs,olevel+1);
                }
            }
        }else{
            for(int i=0;i<csize-clevel;i++){
                char temp=chars[clevel];
                chars[clevel]=chars[clevel+i];
                chars[clevel+i]=temp;
                permutation(chars,clevel+1,oprs,olevel);
                chars[clevel+i]=chars[clevel];
                chars[clevel]=temp;
            }
        }
    }
}

