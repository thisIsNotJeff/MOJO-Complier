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
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%r12
L.64:
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	je L.1
L.2:
	movabsq $64,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movq $8,%rdx
	xorq %rax,%rax
	cmpq %rdx,%rax
	jge L.4
L.3:
	movq %rsi,%rbx
	movq %rax,%rcx
	shlq $3,%rcx
	addq %rcx,%rbx
	movq $0,0(%rbx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %rdx,%rax
	jl L.3
L.4:
	movq %rsi,0(%r12)
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	je L.1
L.5:
	movabsq $64,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movq $8,%rdx
	xorq %rax,%rax
	cmpq %rdx,%rax
	jge L.7
L.6:
	movq %rsi,%rbx
	movq %rax,%rcx
	shlq $3,%rcx
	addq %rcx,%rbx
	movq $0,0(%rbx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %rdx,%rax
	jl L.6
L.7:
	movq %rsi,8(%r12)
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	je L.1
L.8:
	movabsq $120,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rdx,%rax
	jge L.10
L.9:
	movq %rsi,%rbx
	movq %rax,%rcx
	shlq $3,%rcx
	addq %rcx,%rbx
	movq $0,0(%rbx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %rdx,%rax
	jl L.9
L.10:
	movq %rsi,16(%r12)
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	je L.1
L.11:
	movabsq $120,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rdx,%rax
	jge L.13
L.12:
	movq %rsi,%rbx
	movq %rax,%rcx
	shlq $3,%rcx
	addq %rcx,%rbx
	movq $0,0(%rbx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %rdx,%rax
	jl L.12
L.13:
	movq %rsi,24(%r12)
	movq %r12,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
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
	movq %rdi,%r15
	movq %rsi,%r14
L.65:
	movabsq $8,%rax
	cmpq %rax,%r14
	je L.15
L.16:
	xorq %r13,%r13
	movq $7,%rbx
	movq $1,%r12
L.18:
#	movq %r13,%r13
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.26:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.27:
	movq %rcx,%rax
	movq %r13,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.24
L.22:
	movq %r13,%rax
	addq %r12,%rax
	movq %rax,%r13
L.19:
	cmpq %rbx,%r13
	jle L.18
L.20:
L.17:
	jmp L.14
L.15:
	movq %r15,%rdi
	call Queens.Print
	jmp L.17
L.24:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.28:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.29:
	movq %rcx,%rax
	movq %r13,%rcx
	addq %r14,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	jne L.22
L.23:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.30:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.31:
	movq %rcx,%rax
	movq %r13,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r14,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	jne L.22
L.21:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.32:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.33:
	movq %rcx,%rax
	movq %r13,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.35:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.36:
	movq %rcx,%rax
	movq %r13,%rcx
	addq %r14,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.37:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.38:
	movq %rcx,%rax
	movq %r13,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r14,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.39:
	movq 8(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.40:
	movq %rcx,%rax
	movq %r14,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %r13,0(%rax)
	movq -8(%r15),%rax
	movq 8(%rax),%rax
	movq %r15,%rdi
	movq %r14,%rcx
	addq $1,%rcx
	movq %rcx,%rsi
	call *%rax
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.41:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.42:
	movq %rcx,%rax
	movq %r13,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.43:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.44:
	movq %rcx,%rax
	movq %r13,%rcx
	addq %r14,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.25
L.45:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.25
L.46:
#	movq %rcx,%rcx
	movq %r13,%rax
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rax
#	movq %rax,%rax
	subq %r14,%rax
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
	subq $80,%rsp
	movq %r15, -80(%rbp)
	movq %r14, -72(%rbp)
	movq %r13, -64(%rbp)
	movq %r12, -56(%rbp)
	movq %rbx, -48(%rbp)
	movq %rdi,%r13
L.66:
	xorq %rax,%rax
	movq %rax,-32(%rbp)
	movq $7,%r15
	movq $1,%r14
L.48:
	movq -32(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-8(%rbp)
	xorq %rax,%rax
	movq %rax,-24(%rbp)
	movq $7,%rax
	movq %rax,-40(%rbp)
	movq $1,%r12
L.51:
	movq -24(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-16(%rbp)
	movabsq $32,%rax
	movq %rax,%rdi
	call putchar
	movq %r13,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.58
L.59:
	movq 8(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.58
L.60:
	movq %rax,%rbx
	movq -8(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	movq -16(%rbp),%rbx
#	movq %rbx,%rbx
	cmpq %rbx,%rax
	je L.55
L.56:
	movabsq $46,%rax
	movq %rax,%rdi
	call putchar
L.57:
	movq -24(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r12,%rax
#	movq %rax,%rax
	movq %rax,-24(%rbp)
L.52:
	movq -24(%rbp),%rax
#	movq %rax,%rax
	movq -40(%rbp),%rbx
#	movq %rbx,%rbx
	cmpq %rbx,%rax
	jle L.51
L.53:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -32(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r14,%rax
#	movq %rax,%rax
	movq %rax,-32(%rbp)
L.49:
	movq -32(%rbp),%rax
#	movq %rax,%rax
	cmpq %r15,%rax
	jle L.48
L.50:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	jmp L.47
L.55:
	movabsq $81,%rax
	movq %rax,%rdi
	call putchar
	jmp L.57
L.47:
#	returnSink
	movq -48(%rbp),%rbx
	movq -56(%rbp),%r12
	movq -64(%rbp),%r13
	movq -72(%rbp),%r14
	movq -80(%rbp),%r15
	addq $80,%rsp
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
	movabsq $40,%rax
	movq %rax,%rdi
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
	xorq %rax,%rax
	movq %rax,%rsi
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
	leaq L.62(%rip),%rax
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
L.63:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.70:
	leaq L.63(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
