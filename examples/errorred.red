' =========================================================
' errorred.red — Ejemplo con errores semánticos
' Demuestra todos los tipos de error que detecta el compilador
'
' Errores esperados:
'   S2  Línea 14 — interfaz duplicada g0/0 en R1
'   S9  Línea 20 — switch normal con VLAN+IP
'   S1  Línea 24 — dispositivo duplicado PC1
'   S5  Línea 30 — interfaz SW1.f0/2 inexistente
'   S3  Línea 31 — dispositivo PC2 inexistente
'   S5  Línea 32 — interfaz FW1.outside inexistente
'   S7  Línea 34 — R1.g0/0 ya conectada
'   S7  Línea 34 — SW1.f0/1 ya conectada
'   S10 Línea 35 — dispositivo R1 conectado a sí mismo
' =========================================================

red ErrorRed {

    router R1 {
        interfaz g0/0 ip 10.0.0.1/30;
        interfaz g0/0 ip 192.168.1.1/24;    ' S2: interfaz g0/0 duplicada en R1
    }

    switch SW1 {
        interfaz f0/1;
        interfaz vlan10 ip 192.168.1.1/24;  ' S9: switch normal con VLAN+IP
    }

    pc PC1 {
        interfaz eth0 ip 192.168.1.10/24;
    }

    pc PC1 {                                ' S1: dispositivo PC1 duplicado
        interfaz eth1 ip 192.168.1.11/24;
    }

    firewall FW1 {
        interfaz inside ip 192.168.1.254/24;
    }

    ' === Conexiones con errores ===
    conectar PC1.eth0    -> SW1.f0/2;       ' S5: interfaz f0/2 no existe en SW1
    conectar PC2.eth0    -> SW1.f0/1;       ' S3: dispositivo PC2 no existe
    conectar FW1.outside -> R1.g0/0;        ' S5: interfaz outside no existe en FW1
    conectar R1.g0/0     -> SW1.f0/1;       ' OK: primera vez
    conectar R1.g0/0     -> SW1.f0/1;       ' S7: R1.g0/0 y SW1.f0/1 ya conectadas
    conectar R1.g0/0     -> R1.g0/0;        ' S10: R1 no puede conectarse a sí mismo
}
