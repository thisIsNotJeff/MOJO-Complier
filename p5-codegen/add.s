	.text
add:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.6:
	movabsq $1,%rax
	movq %rax,%rbx
	movabsq $0,%rax
#	movq %rax,%rax
	movabsq $0,%rax
#	movq %rax,%rax
	cmpq $0,%rbx
	je L.3
L.1:
	movabsq $1,%rax
#	movq %rax,%rax
	jmp L.2
L.3:
	cmpq $0,%rax
	je L.2
L.7:
	jmp L.1
L.2:
#	movq %rax,%rax
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
L.0:
	call badSub
	.globl _main
	.text
_main:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	call add
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.4:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.9:
	leaq L.4(%rip),%rax
	movq %rax,%rdi
	call _puts
	movabsq $1,%rax
	movq %rax,%rdi
	call _exit
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.5:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.10:
	leaq L.5(%rip),%rax
	movq %rax,%rdi
	call _puts
	movabsq $1,%rax
	movq %rax,%rdi
	call _exit
#	returnSink
	popq %rbp
	ret
