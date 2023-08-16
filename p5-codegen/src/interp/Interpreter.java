/* Copyright (C) 2020, Antony L Hosking and Ben L. Titzer.
 * All rights reserved.  */
package interp;

import Translate.Frag;
import Translate.Temp;
import Translate.Temp.Label;
import Translate.Tree;
import Translate.Tree.Stm;
import Translate.Tree.Exp;
import Translate.Tree.Stm.*;
import Translate.Tree.Exp.*;
import java.util.*;
import java.math.BigInteger;
import java.io.PrintWriter;

/**
 * An interpreter capable of running programs expressed as Tree code.
 */
public class Interpreter {
    private HashMap<Label, LinearTreeCode> procedures = new HashMap<Label, LinearTreeCode>();

    private final DataLayout layout;
    private final boolean trace;
    private static final int RESERVED_PAGE = 4096;
    private static final int MEMORY_WORDS = 256 * 1024;
    private static final int MAX_ALLOCATION = MEMORY_WORDS * Frame.wordSize;

    private static final int EXTERNAL_NEW = -1;
    private static final int EXTERNAL_PUTCHAR = -2;
    private static final int EXTERNAL_GETCHAR = -3;
    private static final int EXTERNAL_BADPTR = -4;
    private static final int EXTERNAL_BADSUB = -5;
    private static final int EXTERNAL_MALLOC = -6;
    private static final int EXTERNAL_EXIT = -7;
    private static final int EXTERNAL_MEMCPY = -8;
    private static final int EXTERNAL_MEMMOVE = -9;
    private static final int FIRST_CODE_ADDRESS = -10;

    private static Integer ZERO = 0;

    // Dynamic state of the machine.
    private int sp;  // stack pointer register
    private int fp;  // frame pointer register
    private int ra;  // return value register
    private int fl;  // frame link register
    private int[] memory;  // memory, including statically allocated data, heap, and stack
    private int brk; // end of heap memory, used for bump-pointer malloc
    private Activation topOfStack;
    private String exception = null;

    public Interpreter(DataLayout layout, List<Frag> frags, boolean trace) {
	this.layout = layout;
	this.trace = trace;
	// Translate all procedures and give them addresses, if they don't already have one.
	for (Frag f : frags) {
	    if (f instanceof Frag.Proc) {
		Frag.Proc p = (Frag.Proc)f;
		LinearTreeCode lc = linearize(p);
		if (trace) dumpCode(lc);
		lc.address = layout.getCodeAddr(p.frame.name);
		procedures.put(p.frame.name, lc);
	    }
	}
    }

    public String run(Frag.Proc p) {
	// initialize memory
	memory = new int[MEMORY_WORDS];
	layout.init(memory);
	// Pass arguments
	sp = fp = memory.length * Frame.wordSize;
	pushFrame(procedures.get(p.frame.name), 0, new int[] {});
	Evaluator eval = new Evaluator();
	while (true) {
	    if (exception != null) return exception;
	    if (topOfStack == null) return "" + ra;  // end of program
	    if (topOfStack.pc >= topOfStack.lc.stms.size()) {
		popFrame();
		continue;
	    }
	    Stm s = topOfStack.lc.stms.get(topOfStack.pc++);
	    s.accept(eval);
	}
    }

    public class Evaluator implements Tree.Visitor<Integer> {
        public Integer visit(SEQ n) {
	    throwError("SEQ() should not occur in linearized code");
	    return ZERO;
	}
        public Integer visit(LABEL n) {
	    return ZERO;  // skip labels
	}
        public Integer visit(JUMP n) {
	    if (trace) print("JUMP(" + n.targets[0] + ")");
	    doGoto(n.targets[0]);
	    return ZERO;
	}
        public Integer visit(MOVE n) {
	    if (n.dst instanceof MEM) {
		MEM m = (MEM)n.dst;
		int addr = eval(m.exp);
		int val = eval(n.src);
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("MOVE(MEM(");
		    buf.append(addr);
		    if (m.offset.value != null) buf.append(" + " + m.offset.value);
		    buf.append("), ");
		    buf.append(val);
		    buf.append(")");
		    print(buf.toString());
		}
		int word = checkMemoryAddr(addr, m.offset.value);
		memory[word] = val;
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("  memory[" + (word * 4) + "] <-- " + val);
		    print(buf.toString());
		}
		return val;
	    }
	    if (n.dst instanceof TEMP) {
		TEMP m = (TEMP)n.dst;
		Activation f = topOfStack;
		int val = eval(n.src);
		f.returnTemp = m.temp;
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("MOVE(TEMP(");
		    buf.append(m.temp);
		    buf.append("), ");
		    buf.append(val);
		    buf.append(")");
		    print(buf.toString());
		}
		f.writeTemp(m.temp, val);
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("  " + m.temp + " <-- " + val);
		    print(buf.toString());
		}
		return val;
	    }
	    throwError("MOVE() to invalid left-hand-side");
	    return ZERO;
	}
        public Integer visit(EXP n) {
	    eval(n.exp);
	    return ZERO;
	}
        public Integer visit(CJUMP n) {
	    int l = eval(n.left);
	    int r = eval(n.right);
	    boolean cond = false;
	    switch (n.op) {
	      case BEQ:
		  cond = (l == r);
		  break;
	      case BNE:
		  cond = (l != r);
		  break;
	      case BGE:
		  cond = (l >= r);
		  break;
	      case BLE:
		  cond = (l <= r);
		  break;
	      case BLT:
		  cond = (l < r);
		  break;
	      case BGT:
		  cond = (l > r);
		  break;
	    }
	    Label target = cond ? n.iftrue : n.iffalse;
	    if (trace) print(n.op + "(" + cond + ") => " + target);
	    doGoto(target);
	    return ZERO;
	}
        public Integer visit(MEM n) {
	    int addr = eval(n.exp);
	    if (trace) {
		StringBuilder buf = new StringBuilder();
		buf.append("MEM(");
		buf.append(addr);
		if (n.offset.value != null) {
		    buf.append(" + ");
		    buf.append(n.offset.value);
		}
		buf.append(")");
		print(buf.toString());
	    }
	    int word = checkMemoryAddr(addr, n.offset.value);
	    int result = memory[word];
	    if (trace) {
		StringBuilder buf = new StringBuilder();
		buf.append("  " + result + " <-- memory[" + (word * 4) + "]");
		print(buf.toString());
	    }
	    return result;
	}
        public Integer visit(TEMP n) {
	    Integer val = topOfStack.readTemp(n.temp);
	    if (trace) {
		print("TEMP(" + n.temp + ")");
		print("  " + val + " <-- " + n.temp);
	    }
	    if (val != null) return val;
	    throwError("Temp(" + n.temp + ") has no value in this frame");
	    return ZERO;
	}
        public Integer visit(ESEQ n) {
	    throwError("ESEQ() should not occur in linearized code");
	    return ZERO;
	}
        public Integer visit(NAME n) {
	    Integer addr = layout.lookup(n.label);
	    if (trace) print("NAME(" + n.label + ") = " + addr);
	    if (addr != null) return addr;
	    throwError("NAME(" + n.label + ") does not have an address");
	    return ZERO;
	}
        public Integer visit(CONST n) {
	    if (trace) print("CONST(" + n.value + ")");
	    return n.value.intValue();
	}
        public Integer visit(CALL n) {
	    topOfStack.returnTemp = null;
	    int func = eval(n.func);
	    int link = eval(n.link);
	    int[] args = new int[n.args.length];
	    for (int i = 0; i < args.length; i++) args[i] = eval(n.args[i]);
	    if (trace) {
		StringBuilder buf = new StringBuilder();
		buf.append("CALL(func=");
		buf.append(func);
		buf.append(", link=");
                buf.append(link);
		appendArgs(buf, args);
		buf.append(")");
		print(buf.toString());
	    }
            switch (func) {
            case EXTERNAL_NEW:     return callNew(args);
            case EXTERNAL_PUTCHAR: return callPutchar(args);
            case EXTERNAL_GETCHAR: return callGetchar(args);
            case EXTERNAL_MALLOC:  return callMalloc(args);
            case EXTERNAL_EXIT:    return callExit(args);
            case EXTERNAL_MEMCPY:  return callMemcpy(args);
            case EXTERNAL_MEMMOVE: return callMemmove(args);
            default: {
                LinearTreeCode lc = addrToCode(func);
                if (lc == null) {
                    throwError("CALL() of invalid function address: " + func);
                    return ZERO;
                }
                pushFrame(lc, link, args);
                return ZERO;
            }
            }
        }
	public Integer visit(BINOP n) {
	    int l = eval(n.left);
	    int r = eval(n.right);
	    int val = 0;
	    switch (n.op) {
	      case ADD:
		  val = l + r;
		  break;
	      case AND:
		  val = l & r;
		  break;
	      case DIV:
		  val = l / checkDivisor(r);
		  break;
	      case DIVU:
		  val = (int)((((long)l) & 0xFFFFFFFF) / (((long)checkDivisor(r)) & 0xFFFFFFFF));
		  break;
	      case MOD:
		  val = l % checkDivisor(r);
		  break;
	      case MUL:
		  val = l * r;
		  break;
	      case OR:
		  val = l | r;
		  break;
	      case SLL:
		  val = l << r;
		  break;
	      case SRA:
		  val = l >> r;
		  break;
	      case SRL:
		  val = l >>> r;
		  break;
	      case SUB:
		  val = l - r;
		  break;
	      case XOR:
		  val = l ^ r;
		  break;
	    }
	    if (trace) print(n.op + "(" + l + ", " + r + ") = " + val);
	    return val;
	}

	int eval(Tree.Exp e) {
	    return e.accept(this);
	}
	void doGoto(Label l) {
	    Integer offset = topOfStack.lc.labels.get(l);
	    if (offset == null) {
		offset = layout.getCodeAddr(l);
		if (offset != null) {
		    if (offset == EXTERNAL_BADPTR) {
			doException("!NullPointerException");
			return;
		    }
		    if (offset == EXTERNAL_BADSUB) {
			doException("!ArrayIndexOutOfBoundsException");
			return;
		    }
		}
		throwError("Label " + l + " not found in this procedure");
	    }
	    else topOfStack.pc = offset;
	}
	int checkDivisor(int val) {
	    if (val == 0) {
		throwError("divide by 0");
		return 1;
	    }
	    return val;
	}
    }

    void doException(String ex) {
	if (exception == null) exception = ex;
    }

    void throwError(String msg) {
	throw new Error(msg);  // TODO: construct a stacktrace
    }

    int checkMemoryAddr(int addr, BigInteger offset) {
	if (offset != null) addr += offset.intValue();
	if ((addr % Frame.wordSize) != 0) {
	    throwError("memory[" + addr + "] misaligned");
	    return 0;
	}
	addr = addr / Frame.wordSize;
	if (addr < RESERVED_PAGE || addr >= memory.length) {
	    throwError("memory[" + addr + "] out of bounds");
	    return 0;
	}
	return addr;
    }

    int callNew(int[] args) {
	if (args.length < 2) throwError("new() requires at least two arguments");
	int size = args[0];
	int hword = args[1];
	if (trace) {
	    print("=> new(size=" + size + ", hword="+hword+")");
	}
	if (size < 0) throwError("new() with negative size");
	if (size > MAX_ALLOCATION) throwError("Out of memory");
	// Allocate an additional word for the header
	int address = layout.allocBytes(size + Frame.wordSize, this.sp);
	if (address < 0) throwError("Out of memory");
	memory[checkMemoryAddr(address, null)] = hword;
	// Return a pointer to one word beyond the header
	return address + Frame.wordSize;
    }

    int callMalloc(int[] args) {
	if (args.length < 1) throwError("malloc() requires at least one argument");
	int size = args[0];
	if (trace) {
	    print("=> malloc(size=" + size + ")");
	}
	if (size < 0) throwError("malloc() with negative size");
	if (size > MAX_ALLOCATION) throwError("Out of memory");
	int address = layout.allocBytes(size, this.sp);
	if (address < 0) throwError("Out of memory");
	return address;
    }

    int callExit(int[] args) {
	if (args.length < 1) throwError("exit() requires at least one argument");
	int status = args[0];
	if (trace) {
	    print("=> exit(status=" + status + ")");
	}
        System.out.println(status);
        System.exit(status);
        return status;
    }

    int callPutchar(int[] args) {
	if (args.length < 1) throwError("putchar() requires at least one argument");
        int c = args[0];
	if (trace) {
	    print("=> putchar(c=" + c + ")");
	}
        System.out.write(c);
	return 0;
    }

    int callGetchar(int[] args) {
        if (trace) {
	    print("=> getchar");
	}
        try {
            return System.in.read();
        } catch (java.io.IOException e) {
            throw new Error(e.getMessage());
        }
    }

    int callMemcpy(int[] args) {
	if (args.length < 3) throwError("memcpy() requires at least three arguments");
        int dst = args[0];
        int src = args[1];
        int len = args[2];
	if (trace) {
	    print("=> memcpy(dst=" + dst + ", src=" + src + ", len=" + len + ")");
	}
        if (len < 0) throwError("memcpy() with negative length");
        if (len == 0) return dst;
        for (int i = len / Frame.wordSize; --i >= 0; ) {
            memory[checkMemoryAddr(dst, null)]
                = memory[checkMemoryAddr(src, null)];
            dst += Frame.wordSize;
            src += Frame.wordSize;
        }
        return args[0];
    }

    int callMemmove(int[] args) {
	if (args.length < 3) throwError("memmove() requires at least three arguments");
        int dst = args[0];
        int src = args[1];
        int len = args[2];
	if (trace) {
	    print("=> memmove(dst=" + dst + ", src=" + src + ", len=" + len + ")");
	}
        if (len < 0) throwError("memmove() with negative length");
        if (len == 0) return dst;
        checkMemoryAddr(dst + len - Frame.wordSize, null);
        checkMemoryAddr(src + len - Frame.wordSize, null);
        System.arraycopy(memory, checkMemoryAddr(src, null),
                         memory, checkMemoryAddr(dst, null),
                         len / Frame.wordSize);
        return args[0];
    }

    void print(String s) {
	for (Activation a = topOfStack; a != null; a = a.caller) {
	    System.out.print("  ");
	}
	System.out.println(s);
    }

    void appendArgs(StringBuilder buf, int[] args) {
	for (Integer i : args) {
	    buf.append(", ");
	    buf.append(i);
	}
    }

    void pushFrame(LinearTreeCode lc, int link, int[] args) {
	// Print the call first
	Frame frame = (Frame)lc.proc.frame;
	int frameSize = frame.frameSize();
	if (frameSize < 0) frameSize = 0;

	// Push a new activation
	Activation a = new Activation();
	a.caller = topOfStack;
	a.lc = lc;
	a.caller_fp = this.fp;
	this.fp = this.sp;
	this.sp -= frameSize * Frame.wordSize;
	a.pc = 0;
	topOfStack = a;

	if (trace) {
	    StringBuilder buf = new StringBuilder();
	    buf.append("=> ");
	    buf.append(lc.proc.frame.name);
	    buf.append("(");
	    appendArgs(buf, args);
	    buf.append(") frameSize=");
	    buf.append(frameSize);
	    buf.append(", sp=");
	    buf.append(this.sp);
	    buf.append(", fp=");
	    buf.append(this.fp);
	    print(buf.toString());
	}

	// Pass arguments
	Iterator<Translate.Frame.Access> formals = frame.formals.iterator();
	for (int i = 0; formals.hasNext(); i++) {
	    Integer arg = i >= args.length ? ZERO : args[i];
	    topOfStack.writeAccess(formals.next(), arg);
	}
        Translate.Frame.Access l = frame.link();
        if (l != null)
            topOfStack.writeAccess(frame.link(), link);
    }

    void popFrame() {
	if (topOfStack == null) return;
	this.sp = this.fp;
	this.fp = topOfStack.caller_fp;
	topOfStack = topOfStack.caller;
	if (topOfStack != null) {
	    if (topOfStack.returnTemp != null) {
		topOfStack.writeTemp(topOfStack.returnTemp, this.ra);
	    }
	}
	if (trace) {
	    StringBuilder buf = new StringBuilder();
	    buf.append("<=  sp=");
	    buf.append(this.sp);
	    buf.append(", fp=");
	    buf.append(this.fp);
	    print(buf.toString());
	}
    }

    private LinearTreeCode addrToCode(int addr) {
	Label l = layout.addrToLabel(addr);
	if (trace) print("  addrToCode(" + addr + ") = " + l);
	if (l == null) return null;
	return procedures.get(l);
    }

    // An activation of a procedure.
    private class Activation {
	Activation caller;
	HashMap<Temp, Integer> tempMap = new HashMap<Temp, Integer>(); // values of temps
	LinearTreeCode lc;
	Temp returnTemp; // return temporary
	int caller_fp;  // caller's frame pointer
	int pc;  // offset into linear tree code for return address

	Integer readTemp(Temp t) {
	    String name = t.toString();
	    if (name.equals("%fp")) return Interpreter.this.fp;
	    if (name.equals("%ra")) return Interpreter.this.ra;
	    if (name.equals("%sp")) return Interpreter.this.sp;
	    if (name.equals("%fl")) return Interpreter.this.fl;
	    return tempMap.get(t);
	}
	void writeTemp(Temp t, int val) {
	    String name = t.toString();
	    if (name.equals("%fp")) Interpreter.this.fp = val;
	    else if (name.equals("%ra")) Interpreter.this.ra = val;
	    else if (name.equals("%sp")) Interpreter.this.sp = val;
	    else if (name.equals("%fl")) Interpreter.this.fl = val;
	    else tempMap.put(t, val);
	}
	void writeAccess(Translate.Frame.Access access, int arg) {
	    if (access instanceof Frame.InFrame) {
		Frame.InFrame af = (Frame.InFrame)access;
		int addr = Interpreter.this.fp + af.offset;
		int word = checkMemoryAddr(addr, null);
		memory[word] = arg;
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("  memory[" + (word * 4) + "] <-- " + arg);
		    print(buf.toString());
		}
	    } else {
		Frame.InReg ar = (Frame.InReg)access;
		topOfStack.writeTemp(ar.temp, arg);
		if (trace) {
		    StringBuilder buf = new StringBuilder();
		    buf.append("  " + ar.temp + " <-- " + arg);
		    print(buf.toString());
		}
	    }
	}
    }

    /**
     * The static mapping of labels to addresses in memory, including what initialized data
     * goes where. Procedures are also given (negative) addresses in memory as needed, though
     * their code is not stored in memory.
     */
    public static class DataLayout {
	// Initialized data loaded into memory.
	private static class InitData {
	    final int address;
	    final int[] data;
	    InitData(int address, int[] data) {
		this.address = address;
		this.data = data;
	    }
	}

	private int address = RESERVED_PAGE * Frame.wordSize;  // don't allocate addresses on first page
	private int codeaddress = FIRST_CODE_ADDRESS;  // give code labels negative addresses
	private final List<InitData> inits = new LinkedList<InitData>();
	private final HashMap<String, Integer> map = new HashMap<String, Integer>();
	private final HashMap<Integer, Label> reverseMap = new HashMap<Integer, Label>();

	public DataLayout() {
	    map.put("new", EXTERNAL_NEW);
	    map.put("putchar", EXTERNAL_PUTCHAR);
	    map.put("getchar", EXTERNAL_GETCHAR);
	    map.put("badPtr", EXTERNAL_BADPTR);
	    map.put("badSub", EXTERNAL_BADSUB);
            map.put("malloc", EXTERNAL_MALLOC);
            map.put("memcpy", EXTERNAL_MEMCPY);
            map.put("memmove", EXTERNAL_MEMMOVE);
            map.put("exit", EXTERNAL_EXIT);
	}

	public String addString(Label lab, String string) {
	    byte[] raw = string.getBytes();
	    int words = (raw.length + 4) / 4; // pad by one byte and round up
	    byte[] b = new byte[words*4];
	    System.arraycopy(b, 0, raw, 0, raw.length);
	    int address = alloc(lab, words);
	    int[] data = new int[words];
	    copyBytesIntoIntArray(data, 0, b);
	    inits.add(new InitData(address, data));
	    return "@" + address + ":" + lab + ":string=" + string;
	}

	public String addRecord(Label lab, int words) {
	    int address = alloc(lab, words);
	    return "@" + address + ":" + lab + ":r[" + words + "]";
	}

	public String addVtable(Label lab, Collection<Label> values) {
	    int words = values.size();
	    int address = alloc(lab, words);
	    int[] data = new int[words];
	    int i = 0;
	    for (Label l : values) {
		data[i++] = getCodeAddr(l);
	    }
	    inits.add(new InitData(address, data));
	    return "@" + address + ":" + lab + ":vtable[" + words + "]";
	}

	void init(int[] memory) {
	    for (InitData d : inits) {
		System.arraycopy(d.data, 0, memory, d.address / Frame.wordSize, d.data.length);
	    }
	}

        int getCodeAddr(Label l) {
	    Integer addr = map.get(l.toString());
	    if (addr == null) {
		addr = codeaddress--;
		map.put(l.toString(), addr);
		reverseMap.put(addr, l);
	    }
	    return addr;
	}

	Label addrToLabel(int addr) {
	    return reverseMap.get(addr);
	}

	Integer lookup(Label l) {
	    return map.get(l.toString());
	}

	private int alloc(Label l, int words) {
	    int result = address;
	    address += (words * Frame.wordSize);
	    map.put(l.toString(), result);
	    reverseMap.put(result, l);
	    return result;
	}

	int allocBytes(int bytes, int heapEnd) {
	    int words = (bytes + Frame.wordSize - 1) / Frame.wordSize;
	    int result = address;
	    int end = address + words * Frame.wordSize;
	    if (end > heapEnd) return -1;
	    address = end;
	    return result;
	}
    }

    private static void copyBytesIntoIntArray(int[] dest, int offset, byte[] b) {
	// encode bytes of string into words.
	for (int i = 0; i < b.length / 4; i++) {
	    int b0 = b[offset+i*4+0] & 0xff;
	    int b1 = b[offset+i*4+1] & 0xff;
	    int b2 = b[offset+i*4+2] & 0xff;
	    int b3 = b[offset+i*4+3] & 0xff;
	    dest[i] = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	}
    }

    /**
     * It's easier to interpret the code if it's been linearized into a list
     * of statements that can be indexed by an integer.
     */
    static class LinearTreeCode {
	Frag.Proc proc;
	int address;
	ArrayList<Tree.Stm> stms = new ArrayList<Tree.Stm>();
	HashMap<Label, Integer> labels = new HashMap<Label, Integer>();
    }

    static void dumpCode(LinearTreeCode lc) {
	PrintWriter out = new PrintWriter(System.out);
	out.println(lc.proc.frame.name + " (linearized):");
	int i = 0;
	for (Tree.Stm s : lc.stms) {
	    out.print(i + ": \n");
	    new Tree.Print(out, s);
	    i++;
	}
	out.println();
	out.flush();
    }

    static LinearTreeCode linearize(Frag.Proc p) {
	LinearTreeCode lc = new LinearTreeCode();
	lc.proc = p;
	new Linearizer(lc.stms, p.body);
	for (int i = 0; i < lc.stms.size(); i++) {
	    Tree.Stm s = lc.stms.get(i);
	    if (s instanceof LABEL) {
		LABEL l = (LABEL)s;
		lc.labels.put(l.label, i);
	    }
	}
	return lc;
    }

    private static class Linearizer implements Tree.Visitor<Exp> {
	final ArrayList<Stm> stms;

	public Linearizer(ArrayList<Tree.Stm> results, Stm s) {
	    this.stms = results;
	    s.accept(this);
	}

	static boolean commute(List<Stm> a, Tree.Exp b) {
	    return a.isEmpty() || b instanceof NAME || b instanceof CONST;
	}

	public Exp visit(SEQ s) {
	    s.left.accept(this);
	    s.right.accept(this);
	    return null;
	}

	public Exp visit(MOVE s) {
	    if (s.dst instanceof TEMP && s.src instanceof CALL) {
		Exp[] kids = s.src.kids();
		reorder(Arrays.asList(kids), stms);
		stms.add(new MOVE(s.dst, s.src.build(kids)));
	    } else if (s.dst instanceof ESEQ) {
		((ESEQ)s.dst).stm.accept(this);
		new MOVE(((ESEQ)s.dst).exp, s.src).accept(this);
	    } else {
		reorder(s);
	    }
	    return null;
	}

	public Exp visit(EXP s) {
	    if (s.exp instanceof CALL) {
		Exp[] kids = s.exp.kids();
		reorder(Arrays.asList(kids), stms);
		stms.add(new EXP(s.exp.build(kids)));
	    } else {
		reorder(s);
	    }
	    return null;
	}

	public Exp visit(LABEL s) {
	    reorder(s);
	    return null;
	}

	public Exp visit(JUMP s) {
	    reorder(s);
	    return null;
	}

	public Exp visit(CJUMP s) {
	    reorder(s);
	    return null;
	}

	private void reorder(Stm s) {
	    Exp[] kids = s.kids();
	    reorder(Arrays.asList(kids), stms);
	    stms.add(s.build(kids));
	}

	public Exp visit(ESEQ e) {
	    e.stm.accept(this);
	    return e.exp.accept(this);
	}

	public Exp visit(MEM e) {
	    return reorder(e);
	}

	public Exp visit(TEMP e) {
	    return reorder(e);
	}

	public Exp visit(NAME e) {
	    return reorder(e);
	}

	public Exp visit(CONST e) {
	    return reorder(e);
	}

	public Exp visit(CALL e) {
	    return reorder(e);
	}

	public Exp visit(BINOP e) {
	    return reorder(e);
	}

	private Exp reorder(Exp e) {
	    Exp[] kids = e.kids();
	    reorder(Arrays.asList(kids), stms);
	    return e.build(kids);
	}

	private void reorder(List<Exp> exps, List<Stm> l) {
	    if (!exps.isEmpty()) {
		Exp a = exps.get(0);
		if (a instanceof CALL) {
		    Temp t = new Temp();
		    Exp e = new ESEQ(new MOVE(new TEMP(t), a), new TEMP(t));
		    exps.set(0, e);
		    reorder(exps, stms);
		} else {
		    Exp aa = a.accept(this);
		    List<Stm> bb = new ArrayList<Stm>();
		    reorder(exps.subList(1, exps.size()), bb);
		    if (commute(bb, aa)) {
			stms.addAll(bb);
			exps.set(0, aa);
		    } else {
			Temp t = new Temp();
			l.add(new MOVE(new TEMP(t), aa));
			l.addAll(bb);
			exps.set(0, new TEMP(t));
		    }
		}
	    }
	}
    }
}
