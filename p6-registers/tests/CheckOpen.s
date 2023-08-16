	.text
CheckOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $1168,%rsp
	movq %r15, -1160(%rbp)
	movq %r14, -1152(%rbp)
	movq %r13, -1144(%rbp)
	movq %r12, -1136(%rbp)
	movq %rbx, -1128(%rbp)
L.51:
	movq $5,%rdi
	xorq %rsi,%rsi
	cmpq %rdi,%rsi
	jge L.1
L.0:
	movq $5,%r9
	xorq %rdx,%rdx
	cmpq %r9,%rdx
	jge L.3
L.2:
	movq $5,%r8
	xorq %rax,%rax
	cmpq %r8,%rax
	jge L.5
L.4:
	movq %rbp,%rcx
	movq %rsi,%rbx
	imulq $200,%rbx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	movq %rdx,%rbx
	imulq $40,%rbx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	movq %rax,%rbx
	shlq $3,%rbx
	addq %rbx,%rcx
	movq $0,-1000(%rcx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %r8,%rax
	jl L.4
L.5:
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
	cmpq %r9,%rdx
	jl L.2
L.3:
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
	cmpq %rdi,%rsi
	jl L.0
L.1:
	movq $5,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.6
L.7:
#	movq %rbx,%rbx
	movq $5,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.6
L.8:
	movq %r12,%rax
	imulq %rbx,%rax
#	movq %rax,%rax
	movq $5,%r13
	xorq %rcx,%rcx
	cmpq %rcx,%r13
	jl L.6
L.9:
	movq %r13,%rcx
	imulq %rax,%rcx
#	movq %rcx,%rcx
	movq $32,%rdi
#	movq %rdi,%rdi
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rcx
	addq $32,%rcx
	movq %rcx,0(%rax)
	movq %rbx,8(%rax)
	movq %r12,16(%rax)
	movq %r13,24(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
#	movq %rcx,%rcx
	movq 16(%rax),%rbx
	imulq %rbx,%rcx
#	movq %rcx,%rcx
#	movq %rcx,%rcx
	movq 24(%rax),%rbx
	imulq %rbx,%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.12
L.10:
	movq %rbx,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.11:
	cmpq %rcx,%rdx
	jl L.10
L.12:
#	movq %rax,%rax
	movq %rax,-1120(%rbp)
	xorq %rax,%rax
	movq $4,%r8
	movq $1,%r9
L.13:
#	movq %rax,%rax
	xorq %rcx,%rcx
	movq $4,%r10
	movq $1,%r11
L.16:
#	movq %rcx,%rcx
	xorq %rbx,%rbx
	movq $4,%r12
	movq $1,%r13
L.19:
#	movq %rbx,%rbx
	movq -1120(%rbp),%rdx
#	movq %rdx,%rdx
#	movq %rdx,%rdx
	xorq %rsi,%rsi
	cmpq %rsi,%rdx
	je L.22
L.23:
	movq %rdx,%rsi
	movq 16(%rsi),%rdx
	movq %rdx,-1016(%rbp)
	movq 24(%rsi),%rdx
	movq %rdx,-1008(%rbp)
	movq %rax,%rdi
	xorq %rdx,%rdx
	cmpq %rdx,%rdi
	jl L.6
L.24:
	movq 8(%rsi),%rdx
	cmpq %rdx,%rdi
	jge L.6
L.25:
	movq %rdi,%rdx
	movq %rdx,%rdi
	movq -1016(%rbp),%rdx
	imulq %rdx,%rdi
	movq %rdi,%rdx
	movq %rdx,%rdi
	movq -1008(%rbp),%rdx
	imulq %rdx,%rdi
	movq %rdi,%rdx
	movq 0(%rsi),%rsi
#	movq %rsi,%rsi
#	movq %rdx,%rdx
	shlq $3,%rdx
	addq %rdx,%rsi
	movq %rsi,-1024(%rbp)
#	movq %rbp,%rbp
	movq -1008(%rbp),%rdx
	movq %rdx,-1032(%rbp)
	movq %rcx,%rdx
	xorq %rsi,%rsi
	cmpq %rsi,%rdx
	jl L.6
L.26:
	movq -1016(%rbp),%rsi
	cmpq %rsi,%rdx
	jge L.6
L.27:
#	movq %rdx,%rdx
#	movq %rdx,%rdx
	movq -1032(%rbp),%rsi
	imulq %rsi,%rdx
#	movq %rdx,%rdx
	movq -1024(%rbp),%rsi
#	movq %rsi,%rsi
#	movq %rdx,%rdx
	shlq $3,%rdx
	addq %rdx,%rsi
	movq %rsi,-1040(%rbp)
#	movq %rbp,%rbp
	movq %rbx,%rdx
	xorq %rsi,%rsi
	cmpq %rsi,%rdx
	jl L.6
L.28:
	movq -1032(%rbp),%rsi
	cmpq %rsi,%rdx
	jge L.6
L.29:
	movq -1040(%rbp),%rsi
	movq %rsi,%rdi
#	movq %rdx,%rdx
	shlq $3,%rdx
	addq %rdx,%rdi
	movq %rax,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
	movq %rcx,%rsi
	addq $1,%rsi
	imulq %rsi,%rdx
#	movq %rdx,%rdx
	movq %rbx,%rsi
	addq $1,%rsi
	imulq %rsi,%rdx
	movq %rdx,0(%rdi)
	movq %rbp,%rdx
	movq %rax,%rsi
	imulq $200,%rsi
#	movq %rsi,%rsi
	addq $-1000,%rsi
	addq %rsi,%rdx
#	movq %rdx,%rdx
	movq %rcx,%rsi
	imulq $40,%rsi
	addq %rsi,%rdx
#	movq %rdx,%rdx
	movq %rbx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdx
	movq %rax,%rsi
	addq $1,%rsi
	movq %rsi,%rdi
	movq %rcx,%rsi
	addq $1,%rsi
	imulq %rsi,%rdi
	movq %rdi,%rsi
	movq %rbx,%rdi
	addq $1,%rdi
	imulq %rdi,%rsi
	movq %rsi,0(%rdx)
#	movq %rbx,%rbx
	addq %r13,%rbx
#	movq %rbx,%rbx
L.20:
	cmpq %r12,%rbx
	jle L.19
L.21:
	movq %rcx,%rbx
	addq %r11,%rbx
	movq %rbx,%rcx
L.17:
	cmpq %r10,%rcx
	jle L.16
L.18:
#	movq %rax,%rax
	addq %r9,%rax
#	movq %rax,%rax
L.14:
	cmpq %r8,%rax
	jle L.13
L.15:
	xorq %rbx,%rbx
	movq $4,%rax
	movq %rax,-1112(%rbp)
	movq $1,%rax
	movq %rax,-1104(%rbp)
L.30:
#	movq %rbx,%rbx
	xorq %r12,%r12
	movq $4,%rax
	movq %rax,-1096(%rbp)
	movq $1,%rax
	movq %rax,-1088(%rbp)
L.33:
#	movq %r12,%r12
	xorq %r13,%r13
	movq $4,%r15
	movq $1,%r14
L.36:
	movq %r13,%rcx
	movq -1120(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	xorq %rdx,%rdx
	cmpq %rdx,%rax
	je L.22
L.42:
#	movq %rax,%rax
	movq 16(%rax),%rdx
	movq %rdx,-1056(%rbp)
	movq 24(%rax),%rdx
	movq %rdx,-1048(%rbp)
	movq %rbx,%rdx
	xorq %rsi,%rsi
	cmpq %rsi,%rdx
	jl L.6
L.43:
	movq 8(%rax),%rsi
	cmpq %rsi,%rdx
	jge L.6
L.44:
#	movq %rdx,%rdx
#	movq %rdx,%rdx
	movq -1056(%rbp),%rsi
	imulq %rsi,%rdx
#	movq %rdx,%rdx
#	movq %rdx,%rdx
	movq -1048(%rbp),%rsi
	imulq %rsi,%rdx
#	movq %rdx,%rdx
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rdx,%rdx
	shlq $3,%rdx
	addq %rdx,%rax
	movq %rax,-1064(%rbp)
#	movq %rbp,%rbp
	movq -1048(%rbp),%rax
	movq %rax,-1072(%rbp)
	movq %r12,%rax
	xorq %rdx,%rdx
	cmpq %rdx,%rax
	jl L.6
L.45:
	movq -1056(%rbp),%rdx
	cmpq %rdx,%rax
	jge L.6
L.46:
#	movq %rax,%rax
#	movq %rax,%rax
	movq -1072(%rbp),%rdx
	imulq %rdx,%rax
#	movq %rax,%rax
	movq -1064(%rbp),%rdx
#	movq %rdx,%rdx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rdx
	movq %rdx,-1080(%rbp)
#	movq %rbp,%rbp
	movq %rcx,%rax
	xorq %rdx,%rdx
	cmpq %rdx,%rax
	jl L.6
L.47:
	movq -1072(%rbp),%rdx
	cmpq %rdx,%rax
	jge L.6
L.48:
	movq -1080(%rbp),%rdx
#	movq %rdx,%rdx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rdx
	movq 0(%rdx),%rsi
	movq %rbp,%rax
	movq %rbx,%rdx
	imulq $200,%rdx
#	movq %rdx,%rdx
	addq $-1000,%rdx
	addq %rdx,%rax
#	movq %rax,%rax
	movq %r12,%rdx
	imulq $40,%rdx
	addq %rdx,%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	cmpq %rax,%rsi
	jne L.39
L.40:
	movabsq $84,%rdi
#	movq %rdi,%rdi
	call putchar
L.41:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq %r13,%rax
	addq %r14,%rax
	movq %rax,%r13
L.37:
	cmpq %r15,%r13
	jle L.36
L.38:
	movq %r12,%rax
	movq -1088(%rbp),%rcx
#	movq %rcx,%rcx
	addq %rcx,%rax
	movq %rax,%r12
L.34:
	movq -1096(%rbp),%rax
#	movq %rax,%rax
	cmpq %rax,%r12
	jle L.33
L.35:
	movq %rbx,%rax
	movq -1104(%rbp),%rbx
#	movq %rbx,%rbx
	addq %rbx,%rax
	movq %rax,%rbx
L.31:
	movq -1112(%rbp),%rax
#	movq %rax,%rax
	cmpq %rax,%rbx
	jle L.30
L.52:
	jmp L.32
L.39:
	movabsq $78,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.41
L.32:
#	returnSink
	movq -1128(%rbp),%rbx
	movq -1136(%rbp),%r12
	movq -1144(%rbp),%r13
	movq -1152(%rbp),%r14
	movq -1160(%rbp),%r15
	addq $1168,%rsp
	popq %rbp
	ret
L.22:
	call badPtr
L.6:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.53:
	call CheckOpen
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.49:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.54:
	leaq L.49(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.50:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.55:
	leaq L.50(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
