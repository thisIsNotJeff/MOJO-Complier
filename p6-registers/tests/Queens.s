	.data
	.balign 8
Queens.Queens:
	.quad Queens.Init
	.quad Queens.Solve
	.text
Queens.Init:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
L.64:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.2:
	movabsq $64,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq $8,%rdi
	xorq %rcx,%rcx
	cmpq %rdi,%rcx
	jge L.4
L.3:
	movq %rax,%rsi
	movq %rcx,%rdx
	shlq $3,%rdx
	addq %rdx,%rsi
	movq $0,0(%rsi)
#	movq %rcx,%rcx
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdi,%rcx
	jl L.3
L.4:
	movq %rax,0(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.5:
	movabsq $64,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq $8,%rdi
	xorq %rcx,%rcx
	cmpq %rdi,%rcx
	jge L.7
L.6:
	movq %rax,%rsi
	movq %rcx,%rdx
	shlq $3,%rdx
	addq %rdx,%rsi
	movq $0,0(%rsi)
#	movq %rcx,%rcx
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdi,%rcx
	jl L.6
L.7:
	movq %rax,8(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.8:
	movabsq $120,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	addq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.10
L.9:
	movq %rax,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
	cmpq %rcx,%rdx
	jl L.9
L.10:
	movq %rax,16(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.11:
	movabsq $120,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	addq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.13
L.12:
	movq %rax,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
	cmpq %rcx,%rdx
	jl L.12
L.13:
	movq %rax,24(%rbx)
	movq %rbx,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
L.1:
	call badPtr
	.text
Queens.Solve:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -40(%rbp)
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
	movq %rsi,%r15
L.65:
	movabsq $8,%rax
	cmpq %rax,%r15
	je L.15
L.16:
	xorq %r14,%r14
	movq $7,%r12
	movq $1,%r13
L.18:
#	movq %r14,%r14
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.26:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.27:
#	movq %rax,%rax
	movq %r14,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.24
L.22:
#	movq %r14,%r14
	addq %r13,%r14
#	movq %r14,%r14
L.19:
	cmpq %r12,%r14
	jle L.18
L.20:
L.17:
	jmp L.14
L.15:
	movq %rbx,%rdi
	call Queens.Print
	jmp L.17
L.24:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.28:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.29:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.22
L.23:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.30:
	movq 24(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.31:
#	movq %rax,%rax
	movq %r14,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r15,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.22
L.21:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.32:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.33:
#	movq %rax,%rax
	movq %r14,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.35:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.36:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.37:
	movq 24(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.38:
#	movq %rax,%rax
	movq %r14,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r15,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.39:
	movq 8(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.40:
#	movq %rax,%rax
	movq %r15,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %r14,0(%rax)
	movq -8(%rbx),%rax
	movq 8(%rax),%rax
	movq %rbx,%rdi
	movq %r15,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
	call *%rax
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.41:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.42:
#	movq %rax,%rax
	movq %r14,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.43:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.44:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.25
L.45:
	movq 24(%rbx),%rcx
#	movq %rcx,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.46:
#	movq %rcx,%rcx
	movq %r14,%rax
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rax
#	movq %rax,%rax
	subq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	jmp L.22
L.14:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	movq -40(%rbp),%r15
	addq $48,%rsp
	popq %rbp
	ret
L.25:
	call badPtr
L.34:
	call badSub
	.text
Queens.Print:
	pushq %rbp
	movq %rsp,%rbp
	subq $64,%rsp
	movq %r15, -56(%rbp)
	movq %r14, -48(%rbp)
	movq %r13, -40(%rbp)
	movq %r12, -32(%rbp)
	movq %rbx, -24(%rbp)
#	movq %rdi,%rdi
	movq %rdi,-16(%rbp)
L.66:
	xorq %rbx,%rbx
	movq $7,%rax
	movq %rax,-8(%rbp)
	movq $1,%r15
L.48:
#	movq %rbx,%rbx
	xorq %r12,%r12
	movq $7,%r13
	movq $1,%r14
L.51:
#	movq %r12,%r12
	movabsq $32,%rdi
#	movq %rdi,%rdi
	call putchar
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.58
L.59:
	movq 8(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.58
L.60:
#	movq %rax,%rax
	movq %rbx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	cmpq %r12,%rax
	je L.55
L.56:
	movabsq $46,%rdi
#	movq %rdi,%rdi
	call putchar
L.57:
	movq %r12,%rax
	addq %r14,%rax
	movq %rax,%r12
L.52:
	cmpq %r13,%r12
	jle L.51
L.53:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq %rbx,%rax
	addq %r15,%rax
	movq %rax,%rbx
L.49:
	movq -8(%rbp),%rax
#	movq %rax,%rax
	cmpq %rax,%rbx
	jle L.48
L.50:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.47
L.55:
	movabsq $81,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.57
L.47:
#	returnSink
	movq -24(%rbp),%rbx
	movq -32(%rbp),%r12
	movq -40(%rbp),%r13
	movq -48(%rbp),%r14
	movq -56(%rbp),%r15
	addq $64,%rsp
	popq %rbp
	ret
L.58:
	call badPtr
L.54:
	call badSub
	.text
Queens:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.67:
	movabsq $40,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	leaq Queens.Queens(%rip),%rbx
	movq %rbx,-8(%rax)
	movq $0,0(%rax)
	movq $0,8(%rax)
	movq $0,16(%rax)
	movq $0,24(%rax)
#	movq %rax,%rax
	movq -8(%rax),%rbx
	movq 0(%rbx),%rbx
	movq %rax,%rdi
	call *%rbx
#	movq %rax,%rax
	movq -8(%rax),%rbx
	movq 8(%rbx),%rbx
	movq %rax,%rdi
	xorq %rsi,%rsi
#	movq %rsi,%rsi
	call *%rbx
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
L.61:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.68:
	call Queens
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.62:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.69:
	leaq L.62(%rip),%rdi
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
L.63:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.70:
	leaq L.63(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
