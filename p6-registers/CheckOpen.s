	.text
CheckOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $1184,%rsp
	movq %r15, -1176(%rbp)
	movq %r14, -1168(%rbp)
	movq %r13, -1160(%rbp)
	movq %r12, -1152(%rbp)
	movq %rbx, -1144(%rbp)
L.51:
	movq $5,%rbx
	xorq %r8,%r8
	cmpq %rbx,%r8
	jge L.1
L.0:
	movq $5,%rdx
	xorq %r9,%r9
	cmpq %rdx,%r9
	jge L.3
L.2:
	movq $5,%rsi
	xorq %rdi,%rdi
	cmpq %rsi,%rdi
	jge L.5
L.4:
	movq %rbp,%rax
	movq %r8,%rcx
	imulq $200,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	movq %r9,%rcx
	imulq $40,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	movq %rdi,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,-1000(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
	cmpq %rsi,%rdi
	jl L.4
L.5:
	movq %r9,%rax
	addq $1,%rax
	movq %rax,%r9
	cmpq %rdx,%r9
	jl L.2
L.3:
	movq %r8,%rax
	addq $1,%rax
	movq %rax,%r8
	cmpq %rbx,%r8
	jl L.0
L.1:
	movq $5,%r14
	xorq %rax,%rax
	cmpq %rax,%r14
	jl L.6
L.7:
	movq %r14,%rax
	movq $5,%r13
	xorq %rbx,%rbx
	cmpq %rbx,%r13
	jl L.6
L.8:
	movq %r13,%rbx
	imulq %rax,%rbx
	movq %rbx,%rax
	movq $5,%r12
	xorq %rbx,%rbx
	cmpq %rbx,%r12
	jl L.6
L.9:
	movq %r12,%rbx
	imulq %rax,%rbx
	movq %rbx,%rax
	movq $32,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $32,%rax
	movq %rax,0(%rsi)
	movq %r14,8(%rsi)
	movq %r13,16(%rsi)
	movq %r12,24(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq %rdi,%rax
	movq 16(%rsi),%rbx
	imulq %rbx,%rax
	movq %rax,%rdi
	movq %rdi,%rax
	movq 24(%rsi),%rbx
	imulq %rbx,%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.12
L.10:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.11:
	cmpq %rdi,%rbx
	jl L.10
L.12:
	movq %rsi,%rbx
	xorq %r10,%r10
	movq $4,%rsi
	movq $1,%r15
L.13:
#	movq %r10,%r10
	xorq %r9,%r9
	movq $4,%r14
	movq $1,%r13
L.16:
#	movq %r9,%r9
	xorq %r8,%r8
	movq $4,%r12
	movq $1,%r11
L.19:
#	movq %r8,%r8
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.22
L.23:
	movq %rbx,%rdi
	movq 16(%rdi),%rax
	movq %rax,-1016(%rbp)
	movq 24(%rdi),%rax
	movq %rax,-1008(%rbp)
	movq %r10,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.6
L.24:
	movq 8(%rdi),%rax
	cmpq %rax,%rcx
	jge L.6
L.25:
	movq %rcx,%rdx
	movq %rdx,%rax
	movq -1016(%rbp),%rcx
	imulq %rcx,%rax
	movq %rax,%rdx
	movq %rdx,%rax
	movq -1008(%rbp),%rcx
	imulq %rcx,%rax
	movq %rax,%rdx
	movq 0(%rdi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,-1024(%rbp)
#	movq %rbp,%rbp
	movq -1008(%rbp),%rax
	movq %rax,-1032(%rbp)
	movq %r9,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.6
L.26:
	movq -1016(%rbp),%rax
	cmpq %rax,%rcx
	jge L.6
L.27:
	movq %rcx,%rdx
	movq %rdx,%rax
	movq -1032(%rbp),%rcx
	imulq %rcx,%rax
	movq %rax,%rdx
	movq -1024(%rbp),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,-1040(%rbp)
	movq %rbp,%rcx
	movq %r8,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.6
L.28:
	movq -1032(%rcx),%rax
	cmpq %rax,%rdx
	jge L.6
L.29:
	movq -1040(%rcx),%rax
	movq %rax,%rdi
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rdi
	movq %r10,%rax
	addq $1,%rax
#	movq %rax,%rax
	movq %r9,%rcx
	addq $1,%rcx
	imulq %rcx,%rax
#	movq %rax,%rax
	movq %r8,%rcx
	addq $1,%rcx
	imulq %rcx,%rax
	movq %rax,0(%rdi)
	movq %rbp,%rcx
	movq %r10,%rax
	imulq $200,%rax
#	movq %rax,%rax
	addq $-1000,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %r9,%rcx
	imulq $40,%rcx
	addq %rcx,%rax
	movq %rax,%rdx
	movq %r8,%rax
	shlq $3,%rax
	addq %rax,%rdx
	movq %r10,%rax
	addq $1,%rax
#	movq %rax,%rax
	movq %r9,%rcx
	addq $1,%rcx
	imulq %rcx,%rax
	movq %rax,%rcx
	movq %r8,%rax
	addq $1,%rax
	imulq %rax,%rcx
	movq %rcx,0(%rdx)
	movq %r8,%rax
	addq %r11,%rax
	movq %rax,%r8
L.20:
	cmpq %r12,%r8
	jle L.19
L.21:
	movq %r9,%rax
	addq %r13,%rax
	movq %rax,%r9
L.17:
	cmpq %r14,%r9
	jle L.16
L.18:
	movq %r10,%rax
	addq %r15,%rax
	movq %rax,%r10
L.14:
	cmpq %rsi,%r10
	jle L.13
L.15:
	xorq %rax,%rax
	movq %rax,-1136(%rbp)
	movq $4,%r12
	movq $1,%r13
L.30:
	movq -1136(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-1104(%rbp)
	xorq %rax,%rax
	movq %rax,-1128(%rbp)
	movq $4,%r14
	movq $1,%r15
L.33:
	movq -1128(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-1112(%rbp)
	xorq %rax,%rax
	movq %rax,-1120(%rbp)
	movq $4,%rax
	movq %rax,-1088(%rbp)
	movq $1,%rax
	movq %rax,-1096(%rbp)
L.36:
	movq -1120(%rbp),%rax
#	movq %rax,%rax
	movq %rax,%rdx
	movq %rbx,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.22
L.42:
#	movq %rax,%rax
	movq 16(%rax),%rcx
	movq %rcx,-1056(%rbp)
	movq 24(%rax),%rcx
	movq %rcx,-1048(%rbp)
	movq -1104(%rbp),%rcx
#	movq %rcx,%rcx
	movq %rcx,%rsi
	xorq %rcx,%rcx
	cmpq %rcx,%rsi
	jl L.6
L.43:
	movq 8(%rax),%rcx
	cmpq %rcx,%rsi
	jge L.6
L.44:
	movq %rsi,%rcx
#	movq %rcx,%rcx
	movq -1056(%rbp),%rsi
	imulq %rsi,%rcx
#	movq %rcx,%rcx
#	movq %rcx,%rcx
	movq -1048(%rbp),%rsi
	imulq %rsi,%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %rax,-1064(%rbp)
	movq %rbp,%rax
	movq -1048(%rax),%rcx
	movq %rcx,-1072(%rbp)
	movq -1112(%rbp),%rcx
#	movq %rcx,%rcx
	movq %rcx,%rsi
	xorq %rcx,%rcx
	cmpq %rcx,%rsi
	jl L.6
L.45:
	movq -1056(%rax),%rcx
	cmpq %rcx,%rsi
	jge L.6
L.46:
	movq %rsi,%rcx
#	movq %rcx,%rcx
	movq -1072(%rbp),%rsi
	imulq %rsi,%rcx
#	movq %rcx,%rcx
	movq -1064(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %rax,-1080(%rbp)
#	movq %rbp,%rbp
	movq %rdx,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.6
L.47:
	movq -1072(%rbp),%rax
	cmpq %rax,%rcx
	jge L.6
L.48:
	movq -1080(%rbp),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	movq %rbp,%rax
	movq -1104(%rbp),%rsi
#	movq %rsi,%rsi
#	movq %rsi,%rsi
	imulq $200,%rsi
#	movq %rsi,%rsi
	addq $-1000,%rsi
	addq %rsi,%rax
#	movq %rax,%rax
	movq -1112(%rbp),%rsi
#	movq %rsi,%rsi
#	movq %rsi,%rsi
	imulq $40,%rsi
	addq %rsi,%rax
#	movq %rax,%rax
#	movq %rdx,%rdx
	shlq $3,%rdx
	addq %rdx,%rax
	movq 0(%rax),%rax
	cmpq %rax,%rcx
	jne L.39
L.40:
	movabsq $84,%rax
	movq %rax,%rdi
	call putchar
L.41:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -1120(%rbp),%rax
#	movq %rax,%rax
	movq %rax,%rcx
	movq -1096(%rbp),%rax
#	movq %rax,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,-1120(%rbp)
L.37:
	movq -1088(%rbp),%rax
	movq %rax,%rcx
	movq -1120(%rbp),%rax
#	movq %rax,%rax
	cmpq %rcx,%rax
	jle L.36
L.38:
	movq -1128(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r15,%rax
#	movq %rax,%rax
	movq %rax,-1128(%rbp)
L.34:
	movq -1128(%rbp),%rax
#	movq %rax,%rax
	cmpq %r14,%rax
	jle L.33
L.35:
	movq -1136(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r13,%rax
#	movq %rax,%rax
	movq %rax,-1136(%rbp)
L.31:
	movq -1136(%rbp),%rax
#	movq %rax,%rax
	cmpq %r12,%rax
	jle L.30
L.52:
	jmp L.32
L.39:
	movabsq $78,%rax
	movq %rax,%rdi
	call putchar
	jmp L.41
L.32:
#	returnSink
	movq -1144(%rbp),%rbx
	movq -1152(%rbp),%r12
	movq -1160(%rbp),%r13
	movq -1168(%rbp),%r14
	movq -1176(%rbp),%r15
	addq $1184,%rsp
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
	leaq L.49(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
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
	leaq L.50(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
