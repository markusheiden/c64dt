/**
 * 6502 assembler code to decode gcr.
 *
 * GCR 5 to 4 scheme:
 * 11111222 22333334 44445555 56666677 77788888
 *
 * max 26 cycles
 */

/1 ?2 /3 ?4 ?5 /6 ?7 /8

         ldy #$00
start:
2        bvc self
4        lda gcr1
2        tax
2        and #$1f
5        sta buffer1,y
2        txa
2        and #$e0
2*       lsr
2*       lsr
3        sta temp1
==
26

2        bvc self
4        lda gcr2
5        sta buffer3,y
2        asl
2        and #$05
3        ora temp1
5        sta buffer2,y
==
23

2        bvc self
4        lda gcr3
3        tax
2        rol
2*       and #$1f
5        sta buffer4,y
2        txa
2        and #$f0
2        tax
==
24

2        bvc self
4        lda gcr4
5        sta buffer6,y
2        lsr
2        and #$60
3        sta temp4
2        txa
2        ror
5        sta buffer5,y
==
27

2        bvc self
4        lda gcr5
5        sta buffer8,y
2        and #$03
3        ora temp4
5        sta buffer7,y
==
21 + 5

2        iny
3        bne start
