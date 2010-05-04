// $ANTLR 3.1b1 GCL.g 2010-05-04 16:29:38

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")              
public class GCLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR", "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE", "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=24;
    public static final int FUNCTIONS=6;
    public static final int WHILE=15;
    public static final int BOOL_TYPE=30;
    public static final int NODE_TYPE=29;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=43;
    public static final int ALAP=14;
    public static final int AND=42;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=44;
    public static final int QUOTE=38;
    public static final int T__51=51;
    public static final int COMMA=34;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=28;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int T__47=47;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int NUMBER=41;
    public static final int MINUS=40;
    public static final int INT_TYPE=32;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL_TYPE=33;
    public static final int DONT_CARE=36;
    public static final int WS=46;
    public static final int ANY=26;
    public static final int OUT=35;
    public static final int UNTIL=16;
    public static final int BLOCK=5;
    public static final int STRING_TYPE=31;
    public static final int SL_COMMENT=45;
    public static final int OR=13;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=37;
    public static final int BSLASH=39;

    // delegates
    // delegators


        public GCLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLParser.tokenNames; }
    public String getGrammarFileName() { return "GCL.g"; }

    
        private List<String> errors = new LinkedList<String>();
        public void displayRecognitionError(String[] tokenNames,
                                            RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            errors.add(hdr + " " + msg);
        }
        public List<String> getErrors() {
            return errors;
        }
    
    	CommonTree concat(CommonTree seq) {
            String result;
            List children = seq.getChildren();
            if (children == null) {
                result = seq.getText();
            } else {
                StringBuilder builder = new StringBuilder();
                for (Object token: seq.getChildren()) {
                    builder.append(((CommonTree) token).getText());
                }
                result = builder.toString();
            }
            return new CommonTree(new CommonToken(IDENTIFIER, result));
        }


    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCL.g:64:1: program : ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) ;
    public final GCLParser.program_return program() throws RecognitionException {
        GCLParser.program_return retval = new GCLParser.program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLParser.function_return function1 = null;

        GCLParser.statement_return statement2 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCL.g:64:9: ( ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) )
            // GCL.g:64:11: ( function | statement )*
            {
            // GCL.g:64:11: ( function | statement )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCL.g:64:12: function
            	    {
            	    pushFollow(FOLLOW_function_in_program97);
            	    function1=function();

            	    state._fsp--;

            	    stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCL.g:64:21: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_program99);
            	    statement2=statement();

            	    state._fsp--;

            	    stream_statement.add(statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);



            // AST REWRITE
            // elements: function, statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 64:33: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
            {
                // GCL.g:64:36: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCL.g:64:46: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCL.g:64:58: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCL.g:64:69: ^( BLOCK ( statement )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCL.g:64:77: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_2, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end program

    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // GCL.g:66:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final GCLParser.block_return block() throws RecognitionException {
        GCLParser.block_return retval = new GCLParser.block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        GCLParser.statement_return statement4 = null;


        CommonTree char_literal3_tree=null;
        CommonTree char_literal5_tree=null;
        RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
        RewriteRuleTokenStream stream_47=new RewriteRuleTokenStream(adaptor,"token 47");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // GCL.g:66:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:66:9: '{' ( statement )* '}'
            {
            char_literal3=(Token)match(input,47,FOLLOW_47_in_block129);  
            stream_47.add(char_literal3);

            // GCL.g:66:13: ( statement )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCL.g:66:13: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block131);
            	    statement4=statement();

            	    state._fsp--;

            	    stream_statement.add(statement4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            char_literal5=(Token)match(input,48,FOLLOW_48_in_block135);  
            stream_48.add(char_literal5);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 66:29: -> ^( BLOCK ( statement )* )
            {
                // GCL.g:66:32: ^( BLOCK ( statement )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCL.g:66:40: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end block

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // GCL.g:68:1: function : FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) ;
    public final GCLParser.function_return function() throws RecognitionException {
        GCLParser.function_return retval = new GCLParser.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FUNCTION6=null;
        Token IDENTIFIER7=null;
        Token char_literal8=null;
        Token char_literal9=null;
        GCLParser.block_return block10 = null;


        CommonTree FUNCTION6_tree=null;
        CommonTree IDENTIFIER7_tree=null;
        CommonTree char_literal8_tree=null;
        CommonTree char_literal9_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_FUNCTION=new RewriteRuleTokenStream(adaptor,"token FUNCTION");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:68:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:68:12: FUNCTION IDENTIFIER '(' ')' block
            {
            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function152);  
            stream_FUNCTION.add(FUNCTION6);

            IDENTIFIER7=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function154);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            char_literal8=(Token)match(input,49,FOLLOW_49_in_function156);  
            stream_49.add(char_literal8);

            char_literal9=(Token)match(input,50,FOLLOW_50_in_function158);  
            stream_50.add(char_literal9);

            pushFollow(FOLLOW_block_in_function160);
            block10=block();

            state._fsp--;

            stream_block.add(block10.getTree());


            // AST REWRITE
            // elements: IDENTIFIER, FUNCTION, block
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 68:46: -> ^( FUNCTION IDENTIFIER block )
            {
                // GCL.g:68:49: ^( FUNCTION IDENTIFIER block )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FUNCTION.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
                adaptor.addChild(root_1, stream_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class condition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition
    // GCL.g:70:1: condition : conditionliteral ( OR condition )? ;
    public final GCLParser.condition_return condition() throws RecognitionException {
        GCLParser.condition_return retval = new GCLParser.condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR12=null;
        GCLParser.conditionliteral_return conditionliteral11 = null;

        GCLParser.condition_return condition13 = null;


        CommonTree OR12_tree=null;

        try {
            // GCL.g:71:2: ( conditionliteral ( OR condition )? )
            // GCL.g:71:4: conditionliteral ( OR condition )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_conditionliteral_in_condition179);
            conditionliteral11=conditionliteral();

            state._fsp--;

            adaptor.addChild(root_0, conditionliteral11.getTree());
            // GCL.g:71:21: ( OR condition )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==OR) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // GCL.g:71:22: OR condition
                    {
                    OR12=(Token)match(input,OR,FOLLOW_OR_in_condition182); 
                    OR12_tree = (CommonTree)adaptor.create(OR12);
                    root_0 = (CommonTree)adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition185);
                    condition13=condition();

                    state._fsp--;

                    adaptor.addChild(root_0, condition13.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end condition

    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // GCL.g:74:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );
    public final GCLParser.statement_return statement() throws RecognitionException {
        GCLParser.statement_return retval = new GCLParser.statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ALAP14=null;
        Token WHILE16=null;
        Token char_literal17=null;
        Token char_literal19=null;
        Token DO20=null;
        Token UNTIL22=null;
        Token char_literal23=null;
        Token char_literal25=null;
        Token DO26=null;
        Token DO28=null;
        Token WHILE30=null;
        Token char_literal31=null;
        Token char_literal33=null;
        Token CHOICE35=null;
        Token CH_OR37=null;
        Token char_literal40=null;
        Token char_literal42=null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.condition_return condition24 = null;

        GCLParser.block_return block27 = null;

        GCLParser.block_return block29 = null;

        GCLParser.condition_return condition32 = null;

        GCLParser.ifstatement_return ifstatement34 = null;

        GCLParser.block_return block36 = null;

        GCLParser.block_return block38 = null;

        GCLParser.expression_return expression39 = null;

        GCLParser.var_declaration_return var_declaration41 = null;


        CommonTree ALAP14_tree=null;
        CommonTree WHILE16_tree=null;
        CommonTree char_literal17_tree=null;
        CommonTree char_literal19_tree=null;
        CommonTree DO20_tree=null;
        CommonTree UNTIL22_tree=null;
        CommonTree char_literal23_tree=null;
        CommonTree char_literal25_tree=null;
        CommonTree DO26_tree=null;
        CommonTree DO28_tree=null;
        CommonTree WHILE30_tree=null;
        CommonTree char_literal31_tree=null;
        CommonTree char_literal33_tree=null;
        CommonTree CHOICE35_tree=null;
        CommonTree CH_OR37_tree=null;
        CommonTree char_literal40_tree=null;
        CommonTree char_literal42_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleTokenStream stream_CHOICE=new RewriteRuleTokenStream(adaptor,"token CHOICE");
        RewriteRuleTokenStream stream_CH_OR=new RewriteRuleTokenStream(adaptor,"token CH_OR");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_var_declaration=new RewriteRuleSubtreeStream(adaptor,"rule var_declaration");
        try {
            // GCL.g:75:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration )
            int alt7=8;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // GCL.g:75:4: ALAP block
                    {
                    ALAP14=(Token)match(input,ALAP,FOLLOW_ALAP_in_statement200);  
                    stream_ALAP.add(ALAP14);

                    pushFollow(FOLLOW_block_in_statement202);
                    block15=block();

                    state._fsp--;

                    stream_block.add(block15.getTree());


                    // AST REWRITE
                    // elements: ALAP, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 75:15: -> ^( ALAP block )
                    {
                        // GCL.g:75:18: ^( ALAP block )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_ALAP.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:76:4: WHILE '(' condition ')' ( DO )? block
                    {
                    WHILE16=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement215);  
                    stream_WHILE.add(WHILE16);

                    char_literal17=(Token)match(input,49,FOLLOW_49_in_statement217);  
                    stream_49.add(char_literal17);

                    pushFollow(FOLLOW_condition_in_statement219);
                    condition18=condition();

                    state._fsp--;

                    stream_condition.add(condition18.getTree());
                    char_literal19=(Token)match(input,50,FOLLOW_50_in_statement221);  
                    stream_50.add(char_literal19);

                    // GCL.g:76:28: ( DO )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==DO) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCL.g:76:28: DO
                            {
                            DO20=(Token)match(input,DO,FOLLOW_DO_in_statement223);  
                            stream_DO.add(DO20);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_statement226);
                    block21=block();

                    state._fsp--;

                    stream_block.add(block21.getTree());


                    // AST REWRITE
                    // elements: block, condition, WHILE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 76:38: -> ^( WHILE condition block )
                    {
                        // GCL.g:76:41: ^( WHILE condition block )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_WHILE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:77:4: UNTIL '(' condition ')' ( DO )? block
                    {
                    UNTIL22=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_statement241);  
                    stream_UNTIL.add(UNTIL22);

                    char_literal23=(Token)match(input,49,FOLLOW_49_in_statement243);  
                    stream_49.add(char_literal23);

                    pushFollow(FOLLOW_condition_in_statement245);
                    condition24=condition();

                    state._fsp--;

                    stream_condition.add(condition24.getTree());
                    char_literal25=(Token)match(input,50,FOLLOW_50_in_statement247);  
                    stream_50.add(char_literal25);

                    // GCL.g:77:28: ( DO )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==DO) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // GCL.g:77:28: DO
                            {
                            DO26=(Token)match(input,DO,FOLLOW_DO_in_statement249);  
                            stream_DO.add(DO26);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_statement252);
                    block27=block();

                    state._fsp--;

                    stream_block.add(block27.getTree());


                    // AST REWRITE
                    // elements: UNTIL, block, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 77:38: -> ^( UNTIL condition block )
                    {
                        // GCL.g:77:41: ^( UNTIL condition block )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_UNTIL.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // GCL.g:78:4: DO block WHILE '(' condition ')'
                    {
                    DO28=(Token)match(input,DO,FOLLOW_DO_in_statement267);  
                    stream_DO.add(DO28);

                    pushFollow(FOLLOW_block_in_statement269);
                    block29=block();

                    state._fsp--;

                    stream_block.add(block29.getTree());
                    WHILE30=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement271);  
                    stream_WHILE.add(WHILE30);

                    char_literal31=(Token)match(input,49,FOLLOW_49_in_statement273);  
                    stream_49.add(char_literal31);

                    pushFollow(FOLLOW_condition_in_statement275);
                    condition32=condition();

                    state._fsp--;

                    stream_condition.add(condition32.getTree());
                    char_literal33=(Token)match(input,50,FOLLOW_50_in_statement277);  
                    stream_50.add(char_literal33);



                    // AST REWRITE
                    // elements: condition, block, DO
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 78:37: -> ^( DO block condition )
                    {
                        // GCL.g:78:40: ^( DO block condition )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_DO.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());
                        adaptor.addChild(root_1, stream_condition.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // GCL.g:79:4: ifstatement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_ifstatement_in_statement292);
                    ifstatement34=ifstatement();

                    state._fsp--;

                    adaptor.addChild(root_0, ifstatement34.getTree());

                    }
                    break;
                case 6 :
                    // GCL.g:80:7: CHOICE block ( CH_OR block )*
                    {
                    CHOICE35=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_statement300);  
                    stream_CHOICE.add(CHOICE35);

                    pushFollow(FOLLOW_block_in_statement302);
                    block36=block();

                    state._fsp--;

                    stream_block.add(block36.getTree());
                    // GCL.g:80:20: ( CH_OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // GCL.g:80:21: CH_OR block
                    	    {
                    	    CH_OR37=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_statement305);  
                    	    stream_CH_OR.add(CH_OR37);

                    	    pushFollow(FOLLOW_block_in_statement307);
                    	    block38=block();

                    	    state._fsp--;

                    	    stream_block.add(block38.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: block, CHOICE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 80:35: -> ^( CHOICE ( block )+ )
                    {
                        // GCL.g:80:38: ^( CHOICE ( block )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_CHOICE.nextNode(), root_1);

                        if ( !(stream_block.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_block.hasNext() ) {
                            adaptor.addChild(root_1, stream_block.nextTree());

                        }
                        stream_block.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // GCL.g:81:4: expression ';'
                    {
                    pushFollow(FOLLOW_expression_in_statement323);
                    expression39=expression();

                    state._fsp--;

                    stream_expression.add(expression39.getTree());
                    char_literal40=(Token)match(input,51,FOLLOW_51_in_statement325);  
                    stream_51.add(char_literal40);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 81:19: -> expression
                    {
                        adaptor.addChild(root_0, stream_expression.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 8 :
                    // GCL.g:82:4: var_declaration ';'
                    {
                    pushFollow(FOLLOW_var_declaration_in_statement334);
                    var_declaration41=var_declaration();

                    state._fsp--;

                    stream_var_declaration.add(var_declaration41.getTree());
                    char_literal42=(Token)match(input,51,FOLLOW_51_in_statement336);  
                    stream_51.add(char_literal42);



                    // AST REWRITE
                    // elements: var_declaration
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 82:24: -> var_declaration
                    {
                        adaptor.addChild(root_0, stream_var_declaration.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class ifstatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ifstatement
    // GCL.g:85:1: ifstatement : ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) );
    public final GCLParser.ifstatement_return ifstatement() throws RecognitionException {
        GCLParser.ifstatement_return retval = new GCLParser.ifstatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token IF43=null;
        Token char_literal44=null;
        Token char_literal46=null;
        Token ELSE48=null;
        Token TRY50=null;
        Token ELSE52=null;
        GCLParser.condition_return condition45 = null;

        GCLParser.block_return block47 = null;

        GCLParser.elseblock_return elseblock49 = null;

        GCLParser.block_return block51 = null;

        GCLParser.elseblock_return elseblock53 = null;


        CommonTree IF43_tree=null;
        CommonTree char_literal44_tree=null;
        CommonTree char_literal46_tree=null;
        CommonTree ELSE48_tree=null;
        CommonTree TRY50_tree=null;
        CommonTree ELSE52_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_TRY=new RewriteRuleTokenStream(adaptor,"token TRY");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_elseblock=new RewriteRuleSubtreeStream(adaptor,"rule elseblock");
        try {
            // GCL.g:86:5: ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==IF) ) {
                alt10=1;
            }
            else if ( (LA10_0==TRY) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // GCL.g:86:7: IF '(' condition ')' block ( ELSE elseblock )?
                    {
                    IF43=(Token)match(input,IF,FOLLOW_IF_in_ifstatement358);  
                    stream_IF.add(IF43);

                    char_literal44=(Token)match(input,49,FOLLOW_49_in_ifstatement360);  
                    stream_49.add(char_literal44);

                    pushFollow(FOLLOW_condition_in_ifstatement362);
                    condition45=condition();

                    state._fsp--;

                    stream_condition.add(condition45.getTree());
                    char_literal46=(Token)match(input,50,FOLLOW_50_in_ifstatement364);  
                    stream_50.add(char_literal46);

                    pushFollow(FOLLOW_block_in_ifstatement366);
                    block47=block();

                    state._fsp--;

                    stream_block.add(block47.getTree());
                    // GCL.g:86:34: ( ELSE elseblock )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // GCL.g:86:35: ELSE elseblock
                            {
                            ELSE48=(Token)match(input,ELSE,FOLLOW_ELSE_in_ifstatement369);  
                            stream_ELSE.add(ELSE48);

                            pushFollow(FOLLOW_elseblock_in_ifstatement371);
                            elseblock49=elseblock();

                            state._fsp--;

                            stream_elseblock.add(elseblock49.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IF, condition, elseblock, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 86:52: -> ^( IF condition block ( elseblock )? )
                    {
                        // GCL.g:86:55: ^( IF condition block ( elseblock )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_IF.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:86:76: ( elseblock )?
                        if ( stream_elseblock.hasNext() ) {
                            adaptor.addChild(root_1, stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:87:7: TRY block ( ELSE elseblock )?
                    {
                    TRY50=(Token)match(input,TRY,FOLLOW_TRY_in_ifstatement394);  
                    stream_TRY.add(TRY50);

                    pushFollow(FOLLOW_block_in_ifstatement396);
                    block51=block();

                    state._fsp--;

                    stream_block.add(block51.getTree());
                    // GCL.g:87:17: ( ELSE elseblock )?
                    int alt9=2;
                    alt9 = dfa9.predict(input);
                    switch (alt9) {
                        case 1 :
                            // GCL.g:87:18: ELSE elseblock
                            {
                            ELSE52=(Token)match(input,ELSE,FOLLOW_ELSE_in_ifstatement399);  
                            stream_ELSE.add(ELSE52);

                            pushFollow(FOLLOW_elseblock_in_ifstatement401);
                            elseblock53=elseblock();

                            state._fsp--;

                            stream_elseblock.add(elseblock53.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: elseblock, block, TRY
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 87:35: -> ^( TRY block ( elseblock )? )
                    {
                        // GCL.g:87:38: ^( TRY block ( elseblock )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_TRY.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:87:50: ( elseblock )?
                        if ( stream_elseblock.hasNext() ) {
                            adaptor.addChild(root_1, stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ifstatement

    public static class elseblock_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elseblock
    // GCL.g:90:1: elseblock : ( block | ifstatement -> ^( BLOCK ifstatement ) );
    public final GCLParser.elseblock_return elseblock() throws RecognitionException {
        GCLParser.elseblock_return retval = new GCLParser.elseblock_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLParser.block_return block54 = null;

        GCLParser.ifstatement_return ifstatement55 = null;


        RewriteRuleSubtreeStream stream_ifstatement=new RewriteRuleSubtreeStream(adaptor,"rule ifstatement");
        try {
            // GCL.g:91:5: ( block | ifstatement -> ^( BLOCK ifstatement ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==47) ) {
                alt11=1;
            }
            else if ( (LA11_0==IF||LA11_0==TRY) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GCL.g:91:7: block
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_block_in_elseblock435);
                    block54=block();

                    state._fsp--;

                    adaptor.addChild(root_0, block54.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:92:7: ifstatement
                    {
                    pushFollow(FOLLOW_ifstatement_in_elseblock443);
                    ifstatement55=ifstatement();

                    state._fsp--;

                    stream_ifstatement.add(ifstatement55.getTree());


                    // AST REWRITE
                    // elements: ifstatement
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 92:19: -> ^( BLOCK ifstatement )
                    {
                        // GCL.g:92:22: ^( BLOCK ifstatement )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                        adaptor.addChild(root_1, stream_ifstatement.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end elseblock

    public static class conditionliteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionliteral
    // GCL.g:95:1: conditionliteral : ( TRUE | call );
    public final GCLParser.conditionliteral_return conditionliteral() throws RecognitionException {
        GCLParser.conditionliteral_return retval = new GCLParser.conditionliteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE56=null;
        GCLParser.call_return call57 = null;


        CommonTree TRUE56_tree=null;

        try {
            // GCL.g:96:2: ( TRUE | call )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==TRUE) ) {
                alt12=1;
            }
            else if ( (LA12_0==IDENTIFIER) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // GCL.g:96:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    TRUE56=(Token)match(input,TRUE,FOLLOW_TRUE_in_conditionliteral470); 
                    TRUE56_tree = (CommonTree)adaptor.create(TRUE56);
                    adaptor.addChild(root_0, TRUE56_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:96:11: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_conditionliteral474);
                    call57=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call57.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end conditionliteral

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // GCL.g:98:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression() throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR59=null;
        GCLParser.expression2_return expression258 = null;

        GCLParser.expression_return expression60 = null;


        CommonTree OR59_tree=null;

        try {
            // GCL.g:99:2: ( expression2 ( OR expression )? )
            // GCL.g:99:4: expression2 ( OR expression )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression2_in_expression485);
            expression258=expression2();

            state._fsp--;

            adaptor.addChild(root_0, expression258.getTree());
            // GCL.g:99:16: ( OR expression )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==OR) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // GCL.g:99:17: OR expression
                    {
                    OR59=(Token)match(input,OR,FOLLOW_OR_in_expression488); 
                    OR59_tree = (CommonTree)adaptor.create(OR59);
                    root_0 = (CommonTree)adaptor.becomeRoot(OR59_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression491);
                    expression60=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression60.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression

    public static class expression2_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression2
    // GCL.g:102:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2() throws RecognitionException {
        GCLParser.expression2_return retval = new GCLParser.expression2_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PLUS62=null;
        Token STAR63=null;
        Token SHARP64=null;
        GCLParser.expression_atom_return expression_atom61 = null;

        GCLParser.expression_atom_return expression_atom65 = null;


        CommonTree PLUS62_tree=null;
        CommonTree STAR63_tree=null;
        CommonTree SHARP64_tree=null;

        try {
            // GCL.g:103:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==IDENTIFIER||(LA15_0>=ANY && LA15_0<=OTHER)||LA15_0==49) ) {
                alt15=1;
            }
            else if ( (LA15_0==SHARP) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // GCL.g:103:7: expression_atom ( PLUS | STAR )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression2507);
                    expression_atom61=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom61.getTree());
                    // GCL.g:103:23: ( PLUS | STAR )?
                    int alt14=3;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==PLUS) ) {
                        alt14=1;
                    }
                    else if ( (LA14_0==STAR) ) {
                        alt14=2;
                    }
                    switch (alt14) {
                        case 1 :
                            // GCL.g:103:24: PLUS
                            {
                            PLUS62=(Token)match(input,PLUS,FOLLOW_PLUS_in_expression2510); 
                            PLUS62_tree = (CommonTree)adaptor.create(PLUS62);
                            root_0 = (CommonTree)adaptor.becomeRoot(PLUS62_tree, root_0);


                            }
                            break;
                        case 2 :
                            // GCL.g:103:32: STAR
                            {
                            STAR63=(Token)match(input,STAR,FOLLOW_STAR_in_expression2515); 
                            STAR63_tree = (CommonTree)adaptor.create(STAR63);
                            root_0 = (CommonTree)adaptor.becomeRoot(STAR63_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:104:7: SHARP expression_atom
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    SHARP64=(Token)match(input,SHARP,FOLLOW_SHARP_in_expression2526); 
                    SHARP64_tree = (CommonTree)adaptor.create(SHARP64);
                    root_0 = (CommonTree)adaptor.becomeRoot(SHARP64_tree, root_0);

                    pushFollow(FOLLOW_expression_atom_in_expression2529);
                    expression_atom65=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom65.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression2

    public static class expression_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_atom
    // GCL.g:107:1: expression_atom : ( ANY | OTHER | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom() throws RecognitionException {
        GCLParser.expression_atom_return retval = new GCLParser.expression_atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ANY66=null;
        Token OTHER67=null;
        Token char_literal68=null;
        Token char_literal70=null;
        GCLParser.expression_return expression69 = null;

        GCLParser.call_return call71 = null;


        CommonTree ANY66_tree=null;
        CommonTree OTHER67_tree=null;
        CommonTree char_literal68_tree=null;
        CommonTree char_literal70_tree=null;

        try {
            // GCL.g:108:2: ( ANY | OTHER | '(' expression ')' | call )
            int alt16=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt16=1;
                }
                break;
            case OTHER:
                {
                alt16=2;
                }
                break;
            case 49:
                {
                alt16=3;
                }
                break;
            case IDENTIFIER:
                {
                alt16=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // GCL.g:108:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ANY66=(Token)match(input,ANY,FOLLOW_ANY_in_expression_atom543); 
                    ANY66_tree = (CommonTree)adaptor.create(ANY66);
                    adaptor.addChild(root_0, ANY66_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:109:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OTHER67=(Token)match(input,OTHER,FOLLOW_OTHER_in_expression_atom548); 
                    OTHER67_tree = (CommonTree)adaptor.create(OTHER67);
                    adaptor.addChild(root_0, OTHER67_tree);


                    }
                    break;
                case 3 :
                    // GCL.g:110:4: '(' expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal68=(Token)match(input,49,FOLLOW_49_in_expression_atom553); 
                    pushFollow(FOLLOW_expression_in_expression_atom556);
                    expression69=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression69.getTree());
                    char_literal70=(Token)match(input,50,FOLLOW_50_in_expression_atom558); 

                    }
                    break;
                case 4 :
                    // GCL.g:111:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expression_atom564);
                    call71=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call71.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression_atom

    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start call
    // GCL.g:114:1: call : ruleName ( '(' ( var_list )? ')' )? -> ^( CALL ( var_list )? ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal73=null;
        Token char_literal75=null;
        GCLParser.ruleName_return ruleName72 = null;

        GCLParser.var_list_return var_list74 = null;


        CommonTree char_literal73_tree=null;
        CommonTree char_literal75_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_ruleName=new RewriteRuleSubtreeStream(adaptor,"rule ruleName");
        RewriteRuleSubtreeStream stream_var_list=new RewriteRuleSubtreeStream(adaptor,"rule var_list");
        try {
            // GCL.g:115:2: ( ruleName ( '(' ( var_list )? ')' )? -> ^( CALL ( var_list )? ) )
            // GCL.g:115:4: ruleName ( '(' ( var_list )? ')' )?
            {
            pushFollow(FOLLOW_ruleName_in_call576);
            ruleName72=ruleName();

            state._fsp--;

            stream_ruleName.add(ruleName72.getTree());
            // GCL.g:115:13: ( '(' ( var_list )? ')' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==49) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // GCL.g:115:14: '(' ( var_list )? ')'
                    {
                    char_literal73=(Token)match(input,49,FOLLOW_49_in_call579);  
                    stream_49.add(char_literal73);

                    // GCL.g:115:18: ( var_list )?
                    int alt17=2;
                    alt17 = dfa17.predict(input);
                    switch (alt17) {
                        case 1 :
                            // GCL.g:115:18: var_list
                            {
                            pushFollow(FOLLOW_var_list_in_call581);
                            var_list74=var_list();

                            state._fsp--;

                            stream_var_list.add(var_list74.getTree());

                            }
                            break;

                    }

                    char_literal75=(Token)match(input,50,FOLLOW_50_in_call584);  
                    stream_50.add(char_literal75);


                    }
                    break;

            }



            // AST REWRITE
            // elements: var_list
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 115:34: -> ^( CALL ( var_list )? )
            {
                // GCL.g:115:37: ^( CALL ( var_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1,  concat((ruleName72!=null?((CommonTree)ruleName72.tree):null)) );
                // GCL.g:115:71: ( var_list )?
                if ( stream_var_list.hasNext() ) {
                    adaptor.addChild(root_1, stream_var_list.nextTree());

                }
                stream_var_list.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end call

    public static class ruleName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleName
    // GCL.g:117:1: ruleName : IDENTIFIER ( DOT IDENTIFIER )* ;
    public final GCLParser.ruleName_return ruleName() throws RecognitionException {
        GCLParser.ruleName_return retval = new GCLParser.ruleName_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token IDENTIFIER76=null;
        Token DOT77=null;
        Token IDENTIFIER78=null;

        CommonTree IDENTIFIER76_tree=null;
        CommonTree DOT77_tree=null;
        CommonTree IDENTIFIER78_tree=null;

        try {
            // GCL.g:117:10: ( IDENTIFIER ( DOT IDENTIFIER )* )
            // GCL.g:117:12: IDENTIFIER ( DOT IDENTIFIER )*
            {
            root_0 = (CommonTree)adaptor.nil();

            IDENTIFIER76=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ruleName605); 
            IDENTIFIER76_tree = (CommonTree)adaptor.create(IDENTIFIER76);
            adaptor.addChild(root_0, IDENTIFIER76_tree);

            // GCL.g:117:23: ( DOT IDENTIFIER )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==DOT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // GCL.g:117:24: DOT IDENTIFIER
            	    {
            	    DOT77=(Token)match(input,DOT,FOLLOW_DOT_in_ruleName608); 
            	    DOT77_tree = (CommonTree)adaptor.create(DOT77);
            	    adaptor.addChild(root_0, DOT77_tree);

            	    IDENTIFIER78=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ruleName610); 
            	    IDENTIFIER78_tree = (CommonTree)adaptor.create(IDENTIFIER78);
            	    adaptor.addChild(root_0, IDENTIFIER78_tree);


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ruleName

    public static class var_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_declaration
    // GCL.g:119:1: var_declaration : var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ ;
    public final GCLParser.var_declaration_return var_declaration() throws RecognitionException {
        GCLParser.var_declaration_return retval = new GCLParser.var_declaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token IDENTIFIER80=null;
        Token char_literal81=null;
        Token IDENTIFIER82=null;
        GCLParser.var_type_return var_type79 = null;


        CommonTree IDENTIFIER80_tree=null;
        CommonTree char_literal81_tree=null;
        CommonTree IDENTIFIER82_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // GCL.g:120:2: ( var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ )
            // GCL.g:120:4: var_type IDENTIFIER ( ',' IDENTIFIER )*
            {
            pushFollow(FOLLOW_var_type_in_var_declaration621);
            var_type79=var_type();

            state._fsp--;

            stream_var_type.add(var_type79.getTree());
            IDENTIFIER80=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration623);  
            stream_IDENTIFIER.add(IDENTIFIER80);

            // GCL.g:120:24: ( ',' IDENTIFIER )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==COMMA) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // GCL.g:120:25: ',' IDENTIFIER
            	    {
            	    char_literal81=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_declaration626);  
            	    stream_COMMA.add(char_literal81);

            	    IDENTIFIER82=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration628);  
            	    stream_IDENTIFIER.add(IDENTIFIER82);


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);



            // AST REWRITE
            // elements: IDENTIFIER, var_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 120:42: -> ( ^( VAR var_type IDENTIFIER ) )+
            {
                if ( !(stream_IDENTIFIER.hasNext()||stream_var_type.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_IDENTIFIER.hasNext()||stream_var_type.hasNext() ) {
                    // GCL.g:120:45: ^( VAR var_type IDENTIFIER )
                    {
                    CommonTree root_1 = (CommonTree)adaptor.nil();
                    root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VAR, "VAR"), root_1);

                    adaptor.addChild(root_1, stream_var_type.nextTree());
                    adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_IDENTIFIER.reset();
                stream_var_type.reset();

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end var_declaration

    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_type
    // GCL.g:123:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLParser.var_type_return var_type() throws RecognitionException {
        GCLParser.var_type_return retval = new GCLParser.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set83=null;

        CommonTree set83_tree=null;

        try {
            // GCL.g:124:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCL.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set83=(Token)input.LT(1);
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
                input.consume();
                adaptor.addChild(root_0, (CommonTree)adaptor.create(set83));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end var_type

    public static class var_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_list
    // GCL.g:131:1: var_list : variable ( COMMA var_list )? ;
    public final GCLParser.var_list_return var_list() throws RecognitionException {
        GCLParser.var_list_return retval = new GCLParser.var_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA85=null;
        GCLParser.variable_return variable84 = null;

        GCLParser.var_list_return var_list86 = null;


        CommonTree COMMA85_tree=null;

        try {
            // GCL.g:132:2: ( variable ( COMMA var_list )? )
            // GCL.g:132:4: variable ( COMMA var_list )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_variable_in_var_list684);
            variable84=variable();

            state._fsp--;

            adaptor.addChild(root_0, variable84.getTree());
            // GCL.g:132:13: ( COMMA var_list )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==COMMA) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // GCL.g:132:14: COMMA var_list
                    {
                    COMMA85=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_list687); 
                    pushFollow(FOLLOW_var_list_in_var_list690);
                    var_list86=var_list();

                    state._fsp--;

                    adaptor.addChild(root_0, var_list86.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end var_list

    public static class variable_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable
    // GCL.g:135:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) );
    public final GCLParser.variable_return variable() throws RecognitionException {
        GCLParser.variable_return retval = new GCLParser.variable_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OUT87=null;
        Token IDENTIFIER88=null;
        Token IDENTIFIER89=null;
        Token DONT_CARE90=null;
        GCLParser.literal_return literal91 = null;


        CommonTree OUT87_tree=null;
        CommonTree IDENTIFIER88_tree=null;
        CommonTree IDENTIFIER89_tree=null;
        CommonTree DONT_CARE90_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // GCL.g:136:2: ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) )
            int alt22=4;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // GCL.g:136:4: OUT IDENTIFIER
                    {
                    OUT87=(Token)match(input,OUT,FOLLOW_OUT_in_variable704);  
                    stream_OUT.add(OUT87);

                    IDENTIFIER88=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable706);  
                    stream_IDENTIFIER.add(IDENTIFIER88);



                    // AST REWRITE
                    // elements: OUT, IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 136:19: -> ^( PARAM OUT IDENTIFIER )
                    {
                        // GCL.g:136:22: ^( PARAM OUT IDENTIFIER )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_OUT.nextNode());
                        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:137:4: IDENTIFIER
                    {
                    IDENTIFIER89=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable721);  
                    stream_IDENTIFIER.add(IDENTIFIER89);



                    // AST REWRITE
                    // elements: IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 137:15: -> ^( PARAM IDENTIFIER )
                    {
                        // GCL.g:137:18: ^( PARAM IDENTIFIER )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:138:4: DONT_CARE
                    {
                    DONT_CARE90=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_variable734);  
                    stream_DONT_CARE.add(DONT_CARE90);



                    // AST REWRITE
                    // elements: DONT_CARE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 138:14: -> ^( PARAM DONT_CARE )
                    {
                        // GCL.g:138:17: ^( PARAM DONT_CARE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_DONT_CARE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // GCL.g:139:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_variable747);
                    literal91=literal();

                    state._fsp--;

                    stream_literal.add(literal91.getTree());


                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 139:12: -> ^( PARAM literal )
                    {
                        // GCL.g:139:15: ^( PARAM literal )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end variable

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal
    // GCL.g:142:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE );
    public final GCLParser.literal_return literal() throws RecognitionException {
        GCLParser.literal_return retval = new GCLParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE92=null;
        Token FALSE93=null;
        GCLParser.dqText_return dqText94 = null;

        GCLParser.integer_return integer95 = null;

        GCLParser.real_return real96 = null;


        CommonTree TRUE92_tree=null;
        CommonTree FALSE93_tree=null;
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleSubtreeStream stream_real=new RewriteRuleSubtreeStream(adaptor,"rule real");
        RewriteRuleSubtreeStream stream_integer=new RewriteRuleSubtreeStream(adaptor,"rule integer");
        RewriteRuleSubtreeStream stream_dqText=new RewriteRuleSubtreeStream(adaptor,"rule dqText");
        try {
            // GCL.g:143:2: ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE )
            int alt23=5;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // GCL.g:143:4: TRUE
                    {
                    TRUE92=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal767);  
                    stream_TRUE.add(TRUE92);



                    // AST REWRITE
                    // elements: TRUE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 143:9: -> BOOL_TYPE TRUE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_TRUE.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:144:4: FALSE
                    {
                    FALSE93=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal778);  
                    stream_FALSE.add(FALSE93);



                    // AST REWRITE
                    // elements: FALSE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 144:10: -> BOOL_TYPE FALSE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_FALSE.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:145:4: dqText
                    {
                    pushFollow(FOLLOW_dqText_in_literal789);
                    dqText94=dqText();

                    state._fsp--;

                    stream_dqText.add(dqText94.getTree());


                    // AST REWRITE
                    // elements: dqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 145:11: -> STRING_TYPE dqText
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(STRING_TYPE, "STRING_TYPE"));
                        adaptor.addChild(root_0, stream_dqText.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // GCL.g:146:4: integer
                    {
                    pushFollow(FOLLOW_integer_in_literal800);
                    integer95=integer();

                    state._fsp--;

                    stream_integer.add(integer95.getTree());


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 146:12: -> INT_TYPE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(INT_TYPE, "INT_TYPE"));
                        adaptor.addChild(root_0,  concat((integer95!=null?((CommonTree)integer95.tree):null)) );

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // GCL.g:147:4: real
                    {
                    pushFollow(FOLLOW_real_in_literal811);
                    real96=real();

                    state._fsp--;

                    stream_real.add(real96.getTree());


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 147:9: -> REAL_TYPE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(REAL_TYPE, "REAL_TYPE"));
                        adaptor.addChild(root_0,  concat((real96!=null?((CommonTree)real96.tree):null)) );

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end literal

    public static class dqText_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dqText
    // GCL.g:150:1: dqText : QUOTE dqContent QUOTE ->;
    public final GCLParser.dqText_return dqText() throws RecognitionException {
        GCLParser.dqText_return retval = new GCLParser.dqText_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token QUOTE97=null;
        Token QUOTE99=null;
        GCLParser.dqContent_return dqContent98 = null;


        CommonTree QUOTE97_tree=null;
        CommonTree QUOTE99_tree=null;
        RewriteRuleTokenStream stream_QUOTE=new RewriteRuleTokenStream(adaptor,"token QUOTE");
        RewriteRuleSubtreeStream stream_dqContent=new RewriteRuleSubtreeStream(adaptor,"rule dqContent");
        try {
            // GCL.g:151:4: ( QUOTE dqContent QUOTE ->)
            // GCL.g:151:6: QUOTE dqContent QUOTE
            {
            QUOTE97=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_dqText830);  
            stream_QUOTE.add(QUOTE97);

            pushFollow(FOLLOW_dqContent_in_dqText832);
            dqContent98=dqContent();

            state._fsp--;

            stream_dqContent.add(dqContent98.getTree());
            QUOTE99=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_dqText834);  
            stream_QUOTE.add(QUOTE99);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 151:28: ->
            {
                adaptor.addChild(root_0,  concat((dqContent98!=null?((CommonTree)dqContent98.tree):null)) );

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dqText

    public static class dqContent_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dqContent
    // GCL.g:154:1: dqContent : ( dqTextChar )* ;
    public final GCLParser.dqContent_return dqContent() throws RecognitionException {
        GCLParser.dqContent_return retval = new GCLParser.dqContent_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLParser.dqTextChar_return dqTextChar100 = null;



        try {
            // GCL.g:155:4: ( ( dqTextChar )* )
            // GCL.g:155:6: ( dqTextChar )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCL.g:155:6: ( dqTextChar )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=PROGRAM && LA24_0<=FALSE)||(LA24_0>=BSLASH && LA24_0<=51)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // GCL.g:155:6: dqTextChar
            	    {
            	    pushFollow(FOLLOW_dqTextChar_in_dqContent853);
            	    dqTextChar100=dqTextChar();

            	    state._fsp--;

            	    adaptor.addChild(root_0, dqTextChar100.getTree());

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dqContent

    public static class dqTextChar_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dqTextChar
    // GCL.g:158:1: dqTextChar : (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) );
    public final GCLParser.dqTextChar_return dqTextChar() throws RecognitionException {
        GCLParser.dqTextChar_return retval = new GCLParser.dqTextChar_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set101=null;
        Token BSLASH102=null;
        Token set103=null;

        CommonTree set101_tree=null;
        CommonTree BSLASH102_tree=null;
        CommonTree set103_tree=null;

        try {
            // GCL.g:159:4: (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( ((LA25_0>=PROGRAM && LA25_0<=FALSE)||(LA25_0>=MINUS && LA25_0<=51)) ) {
                alt25=1;
            }
            else if ( (LA25_0==BSLASH) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // GCL.g:159:6: ~ ( QUOTE | BSLASH )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    set101=(Token)input.LT(1);
                    if ( (input.LA(1)>=PROGRAM && input.LA(1)<=FALSE)||(input.LA(1)>=MINUS && input.LA(1)<=51) ) {
                        input.consume();
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(set101));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // GCL.g:160:6: BSLASH ( BSLASH | QUOTE )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    BSLASH102=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_dqTextChar881); 
                    BSLASH102_tree = (CommonTree)adaptor.create(BSLASH102);
                    adaptor.addChild(root_0, BSLASH102_tree);

                    set103=(Token)input.LT(1);
                    if ( (input.LA(1)>=QUOTE && input.LA(1)<=BSLASH) ) {
                        input.consume();
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(set103));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dqTextChar

    public static class real_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start real
    // GCL.g:163:1: real : ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )? ;
    public final GCLParser.real_return real() throws RecognitionException {
        GCLParser.real_return retval = new GCLParser.real_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token n1=null;
        Token n2=null;
        Token MINUS104=null;
        Token DOT105=null;

        CommonTree n1_tree=null;
        CommonTree n2_tree=null;
        CommonTree MINUS104_tree=null;
        CommonTree DOT105_tree=null;

        try {
            // GCL.g:164:2: ( ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )? )
            // GCL.g:164:4: ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )?
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCL.g:164:4: ( MINUS )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==MINUS) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // GCL.g:164:4: MINUS
                    {
                    MINUS104=(Token)match(input,MINUS,FOLLOW_MINUS_in_real900); 
                    MINUS104_tree = (CommonTree)adaptor.create(MINUS104);
                    adaptor.addChild(root_0, MINUS104_tree);


                    }
                    break;

            }

            // GCL.g:164:13: (n1= NUMBER )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==NUMBER) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // GCL.g:164:13: n1= NUMBER
                    {
                    n1=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_real905); 
                    n1_tree = (CommonTree)adaptor.create(n1);
                    adaptor.addChild(root_0, n1_tree);


                    }
                    break;

            }

            DOT105=(Token)match(input,DOT,FOLLOW_DOT_in_real908); 
            DOT105_tree = (CommonTree)adaptor.create(DOT105);
            adaptor.addChild(root_0, DOT105_tree);

            // GCL.g:164:28: (n2= NUMBER )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==NUMBER) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // GCL.g:164:28: n2= NUMBER
                    {
                    n2=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_real912); 
                    n2_tree = (CommonTree)adaptor.create(n2);
                    adaptor.addChild(root_0, n2_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end real

    public static class integer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start integer
    // GCL.g:166:1: integer : ( MINUS )? NUMBER ;
    public final GCLParser.integer_return integer() throws RecognitionException {
        GCLParser.integer_return retval = new GCLParser.integer_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token MINUS106=null;
        Token NUMBER107=null;

        CommonTree MINUS106_tree=null;
        CommonTree NUMBER107_tree=null;

        try {
            // GCL.g:167:2: ( ( MINUS )? NUMBER )
            // GCL.g:167:4: ( MINUS )? NUMBER
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCL.g:167:4: ( MINUS )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==MINUS) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // GCL.g:167:4: MINUS
                    {
                    MINUS106=(Token)match(input,MINUS,FOLLOW_MINUS_in_integer923); 
                    MINUS106_tree = (CommonTree)adaptor.create(MINUS106);
                    adaptor.addChild(root_0, MINUS106_tree);


                    }
                    break;

            }

            NUMBER107=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_integer926); 
            NUMBER107_tree = (CommonTree)adaptor.create(NUMBER107);
            adaptor.addChild(root_0, NUMBER107_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end integer

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA1_eotS =
        "\20\uffff";
    static final String DFA1_eofS =
        "\1\1\17\uffff";
    static final String DFA1_minS =
        "\1\7\17\uffff";
    static final String DFA1_maxS =
        "\1\61\17\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\14\uffff";
    static final String DFA1_specialS =
        "\20\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\uffff\1\3\2\uffff\1\3\1\uffff\4\3\1\uffff\1\3\1\uffff"+
            "\1\3\3\uffff\3\3\1\uffff\5\3\17\uffff\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()* loopback of 64:11: ( function | statement )*";
        }
    }
    static final String DFA2_eotS =
        "\17\uffff";
    static final String DFA2_eofS =
        "\17\uffff";
    static final String DFA2_minS =
        "\1\11\16\uffff";
    static final String DFA2_maxS =
        "\1\61\16\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\14\uffff";
    static final String DFA2_specialS =
        "\17\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\3\uffff"+
            "\3\2\1\uffff\5\2\16\uffff\1\1\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "()* loopback of 66:13: ( statement )*";
        }
    }
    static final String DFA7_eotS =
        "\16\uffff";
    static final String DFA7_eofS =
        "\16\uffff";
    static final String DFA7_minS =
        "\1\11\15\uffff";
    static final String DFA7_maxS =
        "\1\61\15\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\4\uffff\1\10";
    static final String DFA7_specialS =
        "\16\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\4\2\uffff\1\10\1\uffff\1\1\1\2\1\3\1\7\1\uffff\1\5\1\uffff"+
            "\1\5\3\uffff\3\10\1\uffff\5\15\17\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "74:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );";
        }
    }
    static final String DFA6_eotS =
        "\22\uffff";
    static final String DFA6_eofS =
        "\1\1\21\uffff";
    static final String DFA6_minS =
        "\1\7\21\uffff";
    static final String DFA6_maxS =
        "\1\61\21\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\2\17\uffff\1\1";
    static final String DFA6_specialS =
        "\22\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\uffff\1\1\2\uffff\1\1\1\uffff\4\1\1\21\1\1\1\uffff\1"+
            "\1\3\uffff\3\1\1\uffff\5\1\16\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "()* loopback of 80:20: ( CH_OR block )*";
        }
    }
    static final String DFA8_eotS =
        "\22\uffff";
    static final String DFA8_eofS =
        "\1\2\21\uffff";
    static final String DFA8_minS =
        "\1\7\21\uffff";
    static final String DFA8_maxS =
        "\1\61\21\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA8_specialS =
        "\22\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1\2"+
            "\3\uffff\3\2\1\uffff\5\2\16\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "86:34: ( ELSE elseblock )?";
        }
    }
    static final String DFA9_eotS =
        "\22\uffff";
    static final String DFA9_eofS =
        "\1\2\21\uffff";
    static final String DFA9_minS =
        "\1\7\21\uffff";
    static final String DFA9_maxS =
        "\1\61\21\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA9_specialS =
        "\22\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1\2"+
            "\3\uffff\3\2\1\uffff\5\2\16\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "87:17: ( ELSE elseblock )?";
        }
    }
    static final String DFA17_eotS =
        "\13\uffff";
    static final String DFA17_eofS =
        "\13\uffff";
    static final String DFA17_minS =
        "\1\14\12\uffff";
    static final String DFA17_maxS =
        "\1\62\12\uffff";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\10\uffff\1\2";
    static final String DFA17_specialS =
        "\13\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\11\uffff\1\1\5\uffff\1\1\6\uffff\4\1\1\uffff\2\1\10\uffff"+
            "\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "115:18: ( var_list )?";
        }
    }
    static final String DFA22_eotS =
        "\12\uffff";
    static final String DFA22_eofS =
        "\12\uffff";
    static final String DFA22_minS =
        "\1\14\11\uffff";
    static final String DFA22_maxS =
        "\1\51\11\uffff";
    static final String DFA22_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\5\uffff";
    static final String DFA22_specialS =
        "\12\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\11\uffff\1\4\5\uffff\1\4\6\uffff\1\1\1\3\2\4\1\uffff\2"+
            "\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "135:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) );";
        }
    }
    static final String DFA23_eotS =
        "\17\uffff";
    static final String DFA23_eofS =
        "\17\uffff";
    static final String DFA23_minS =
        "\1\26\3\uffff\2\34\1\uffff\1\34\7\uffff";
    static final String DFA23_maxS =
        "\1\51\3\uffff\1\51\1\62\1\uffff\1\62\7\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\1\2\1\3\2\uffff\1\5\3\uffff\1\4\4\uffff";
    static final String DFA23_specialS =
        "\17\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\1\5\uffff\1\6\10\uffff\1\2\1\3\1\uffff\1\4\1\5",
            "",
            "",
            "",
            "\1\6\14\uffff\1\7",
            "\1\6\5\uffff\1\12\17\uffff\1\12",
            "",
            "\1\6\5\uffff\1\12\17\uffff\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "142:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE );";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program97 = new BitSet(new long[]{0x00020003EE2BD282L});
    public static final BitSet FOLLOW_statement_in_program99 = new BitSet(new long[]{0x00020003EE2BD282L});
    public static final BitSet FOLLOW_47_in_block129 = new BitSet(new long[]{0x00030003EE2BD280L});
    public static final BitSet FOLLOW_statement_in_block131 = new BitSet(new long[]{0x00030003EE2BD280L});
    public static final BitSet FOLLOW_48_in_block135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function152 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function154 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_function156 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_function158 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_block_in_function160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition179 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_condition182 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_condition185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement200 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_block_in_statement202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement215 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement217 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement219 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement221 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_DO_in_statement223 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_statement241 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement243 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement245 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement247 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_DO_in_statement249 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement267 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement269 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_WHILE_in_statement271 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement273 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement275 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_statement292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement300 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement302 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_CH_OR_in_statement305 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement307 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_expression_in_statement323 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement334 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_ifstatement358 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_ifstatement360 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_ifstatement362 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_ifstatement364 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_ifstatement366 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement369 = new BitSet(new long[]{0x0000800000280200L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_ifstatement394 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_ifstatement396 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement399 = new BitSet(new long[]{0x0000800000280200L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_elseblock435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_elseblock443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_conditionliteral470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_conditionliteral474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression485 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_expression488 = new BitSet(new long[]{0x000200000E001000L});
    public static final BitSet FOLLOW_expression_in_expression491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2507 = new BitSet(new long[]{0x0000000001800002L});
    public static final BitSet FOLLOW_PLUS_in_expression2510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2526 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_expression_atom_in_expression2529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression_atom543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression_atom548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_expression_atom553 = new BitSet(new long[]{0x000200000E001000L});
    public static final BitSet FOLLOW_expression_in_expression_atom556 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_expression_atom558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleName_in_call576 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_49_in_call579 = new BitSet(new long[]{0x0004037810401000L});
    public static final BitSet FOLLOW_var_list_in_call581 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_call584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ruleName605 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_DOT_in_ruleName608 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ruleName610 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_var_type_in_var_declaration621 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration623 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_COMMA_in_var_declaration626 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration628 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_var_list684 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_COMMA_in_var_list687 = new BitSet(new long[]{0x0000037810401000L});
    public static final BitSet FOLLOW_var_list_in_var_list690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_variable704 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_variable734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_variable747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dqText_in_literal789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integer_in_literal800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_real_in_literal811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_dqText830 = new BitSet(new long[]{0x000FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_dqContent_in_dqText832 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_QUOTE_in_dqText834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dqTextChar_in_dqContent853 = new BitSet(new long[]{0x000FFFBFFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_dqTextChar869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_dqTextChar881 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_set_in_dqTextChar883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_real900 = new BitSet(new long[]{0x0000020010000000L});
    public static final BitSet FOLLOW_NUMBER_in_real905 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_real908 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_NUMBER_in_real912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_integer923 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_NUMBER_in_integer926 = new BitSet(new long[]{0x0000000000000002L});

}