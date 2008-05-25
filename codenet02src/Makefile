CXXFLAGS = -O2 -g -Wall -Wno-deprecated
OBJS = main.o net64.o nethost.o stringops.o argstream.o

all:	codenet

codenet:	$(OBJS)
	$(CXX) $(CXXFLAGS) -o codenet $(OBJS) $(LIBS)

clean:
	rm -f $(OBJS) codenet

