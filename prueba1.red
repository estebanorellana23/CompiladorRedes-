red MiRed {
    router r1 {
        interfaz eth0 ip 192.168.1.1/24;
    }
    pc p1 {
        interfaz eth0 ip 192.168.1.10/24;
    }
    conectar r1.eth0 -> p1.eth0;
}
