red DemoSemantico {

    pc PC1 { interfaz eth0 ip 192.168.1.10/24; }
    pc PC2 { interfaz eth0 ip 192.168.1.11/24; }
    
    laptop L1 { interfaz wlan0; }
    laptop L2 { interfaz wlan0; }
    
    accesspoint AP1 { interfaz wifi; }
    
    switch SW1 {
        interfaz f0/1;
        interfaz f0/2;
    }
    
    conectar PC1.eth0 -> SW1.f0/1;
    conectar PC2.eth0 -> SW1.f0/1;
    
    conectar L1.wlan0 -> AP1.wifi;
    conectar L2.wlan0 -> AP1.wifi;
}
