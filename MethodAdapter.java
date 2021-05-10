import java.io.IOException;

import org.objectweb.asm.*;

public class MethodAdapter extends MethodVisitor implements Opcodes {
    public MethodAdapter(MethodVisitor mv) {
        super(ASM5,mv);
    }
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    	switch (opcode) {
        	case INVOKEVIRTUAL:
        		//check if it is "Thread.start()"
        		if(isThreadClass(owner)&&name.equals("start")&&desc.equals("()V")) {
	            	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logStart",
	        				"(Ljava/lang/Thread;)V",false);
				}//check if it is "Thread.join()"
        		else if(isThreadClass(owner)&&name.equals("join")&&desc.equals("()V")) {
        			mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logJoin", "(Ljava/lang/Thread;)V",false);

    			} //check if it is "Object.wait()"
            	else if(name.equals("wait")&&
                				(desc.equals("()V")||desc.equals("(J)V")||desc.equals("(JI)V"))) {
            		mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logWait", "(Ljava/lang/Object;)V",false);

        		} //check if it is "Object.notify()"
                else if(name.equals("notify")&&desc.equals("()V")) {
                	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logNotify", "(Ljava/lang/Object;)V",false);

            	}//check if it is "Object.notifyAll()"
                else if(name.equals("notifyAll")&&desc.equals("()V")) {
                	mv.visitInsn(DUP);
	        		mv.visitMethodInsn(INVOKESTATIC, "Log", "logNotifyAll", "(Ljava/lang/Object;)V",false);

                				}
        	default: mv.visitMethodInsn(opcode, owner, name, desc,itf);
    	}

    }

    private boolean isThreadClass(String cname)
    {
    	while(!cname.equals("java/lang/Object"))
    	{
    		if(cname.equals("java/lang/Thread"))
    			return true;

    		try {
				ClassReader cr= new ClassReader(cname);
				cname = cr.getSuperName();
			} catch (IOException e) {
				return false;
			}
    	}
    	return false;
    }
    
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    	switch (opcode) {
        case GETSTATIC:
            mv.visitInsn(Opcodes.DCMPL);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log",
                    "logArrayAcc","(Ljava/lang/Object;)V",false);
            
            break;
        case PUTSTATIC:
        	
            mv.visitInsn(Opcodes.SALOAD);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log",
                    "logArrayAcc","(Ljava/lang/Object;)V",false);
            break;
        case GETFIELD:
            mv.visitInsn(Opcodes.F2I);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log",
                    "logArrayAcc","(Ljava/lang/Object;)V",false);
            
            break;
        case PUTFIELD:
            mv.visitInsn(Opcodes.PUTSTATIC);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "Log",
                    "logArrayAcc","(Ljava/lang/Object;)V",false);
            
        default: break;
    }
	    mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    public void visitInsn(int opcode) {
    	switch (opcode) {
		    case AALOAD:case BALOAD:case CALOAD:case SALOAD:case IALOAD:case FALOAD:case
		    DALOAD:case LALOAD:
		    	
		    	mv.visitInsn(DUP2);
		    	mv.visitLdcInsn(0);
		    	mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc", "(Ljava/lang/Object;IZ)V",false);
			    break;
			    
		    case AASTORE:case BASTORE:case CASTORE:case SASTORE:case IASTORE:case FASTORE:
		    	mv.visitInsn(DUP_X2);
		    	mv.visitInsn(POP);
		    	mv.visitInsn(DUP2_X1);
		    	mv.visitLdcInsn(1);
		    	mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc", "(Ljava/lang/Object;IZ)V",false);
			    break;
			    
		    case DASTORE:case LASTORE:
		    	mv.visitInsn(DUP2_X2);
		    	mv.visitInsn(POP2);
		    	mv.visitInsn(DUP2_X2);
		    	mv.visitLdcInsn(1);
		    	mv.visitMethodInsn(INVOKESTATIC, "Log", "logArrayAcc", "(Ljava/lang/Object;IZ)V",false);
			    break;
			    
		    default:
    	}
    	mv.visitInsn(opcode);
    }
    
    
}