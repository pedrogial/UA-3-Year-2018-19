using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Practica4 {

    public partial class Form1 : Form {

        estacion.Station[] stations = new estacion.Station[1];
        estacion.Station st = new estacion.Station();
        int temp, lum, hum;
        bool b;
        int id = 0;
        String registradas = "";

        public Form1() {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e) {}

        //Hacer una consulta Get
        private void button2_Click(object sender, EventArgs e) {
            try {
                String a = comboBox1.Text;
                String c = a.Replace("Estacion ", "");
                int index = Convert.ToInt32(c);
                index--;
                estacion.Station st = stations[index];

                if (comboBox2.Text.Equals("Temperatura")){
                    st.getTem(out temp, out b);
                    richTextBox2.Text = "La temperatura de la estación " + comboBox1.Text + " es: " + temp.ToString() + "\n";
                }
                else
                if (comboBox2.Text.Equals("Humedad")){
                    st.getHum(out hum, out b);
                    richTextBox2.Text = "La humedad de la estación " + comboBox1.Text + " es: " + hum.ToString() + "\n";
                }
                else
                if (comboBox2.Text.Equals("Luminosidad")){
                    st.getLum(out lum, out b);
                    richTextBox2.Text = "La luminosidad de la estación " + comboBox1.Text + " es: " + lum.ToString() + "\n";
                }
                else 
                if (comboBox2.Text.Equals("LCD")){
                    richTextBox2.Text = "El display de la estación " + comboBox1.Text + " es: " + st.getPan() + "\n";
                }
                else
                if (comboBox2.Text.Equals("Todo")){
                    st.getTem(out temp, out b);
                    st.getHum(out hum, out b);
                    st.getLum(out lum, out b);
                    richTextBox2.Text = "La temperatura de la estación " + comboBox1.Text + " es: " + temp.ToString() + "\n";
                    richTextBox2.Text += "La humedad de la estación " + comboBox1.Text + " es: " + hum.ToString() + "\n";
                    richTextBox2.Text += "La luminosidad de la estación " + comboBox1.Text + " es: " + lum.ToString() + "\n";
                    richTextBox2.Text += "El display de la estación " + comboBox1.Text + " es: " + st.getPan();
                }
                else { 
                    richTextBox2.Text = "No se ha seleccionado ninguna consulta";
                }
            }
            catch (Exception ex) {
                richTextBox2.Text = "Se ha producido un error: \n" + ex;
            }
        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        //Hacer una Consulta Set
        private void button3_Click(object sender, EventArgs e) {
            String a = comboBox1.Text;
            String c = a.Replace("Estacion ", "");
            int index = Convert.ToInt32(c);
            index--;
            estacion.Station st = stations[index];

            if (comboBox2.Text.Equals("Temperatura")) {
                st.setTem(Int32.Parse(richTextBox3.Text), true);

            }
            if (comboBox2.Text.Equals("Humedad")) {
                st.setHum(Int32.Parse(richTextBox3.Text), true);

            }
            if (comboBox2.Text.Equals("Luminosidad")) {
                st.setLum(Int32.Parse(richTextBox3.Text), true);

            }
            if (comboBox2.Text.Equals("LCD")) {
                st.setPan(richTextBox3.Text);
            }

        }

        private void button1_Click(object sender, EventArgs e) {
            try {
                richTextBox1.Clear();

                if (!textBox1.Text.Contains(":")) {
                    //UDDI
                    //Patron de búsqueda de servicios
                    juddi.find_service servicio = new juddi.find_service();
                    
                    //service.generic="2.0";       ##En el caso de usar una version anterior a juddi v3
                    servicio.findQualifiers = new string[] { "approximateMatch" };
                    servicio.name = new juddi.name[] { new juddi.name() };
                    servicio.name[0].Value = "Estacion%";

                    //Inquire
                    juddi.UDDIInquiryService inquire = new juddi.UDDIInquiryService();
                    inquire.Url = "http://" + textBox1.Text + ":8081/juddiv3/services/inquiry";

                    //Lista de coincidencias
                    juddi.serviceList lista;
                    lista = inquire.find_service(servicio);

                    //Construye el array de estaciones
                    int estacionesRegistradas = lista.serviceInfos.Length;

                    for (int i = 0; i < estacionesRegistradas; i++) {
                        juddi.serviceInfo informacion = lista.serviceInfos[i];

                        juddi.get_serviceDetail gsd = new juddi.get_serviceDetail();
                        
                        //gsd.generic="2.0"; ##En el caso de no usar juddi v3
                        gsd.serviceKey = new string[] { informacion.serviceKey };

                        juddi.serviceDetail sd = new juddi.serviceDetail();
                        sd = inquire.get_serviceDetail(gsd);

                        juddi.businessService[] bs = sd.businessService;
                        juddi.bindingTemplate[] bTemplate = bs[0].bindingTemplates;
                        juddi.accessPoint AP = bTemplate[0].accessPoint;

                        estacion.Station st = new estacion.Station();

                        st.Url = AP.Value;

                        stations[id] = st;
                       
                        //Obtener ip y puerto del servicio a consumir                       
                        String ip = AP.Value;
                        String[] aux2 = ip.Split(new char[] { '/' });
                        ip = aux2[2];
                        registradas += "Estacion " + (id + 1).ToString() + " -> " + ip + "\n";
                        richTextBox1.Text = registradas;

                        id++;
                        comboBox1.Items.Add("Estacion " + id);
                        
                        estacion.Station[] aux = new estacion.Station[id + 1];

                        //Copia el antiguo y lo mete en el nuevo
                        for (int j = 0; j < stations.Length; j++) {
                            aux[j] = stations[j];
                        }
                        stations = new estacion.Station[id + 1];
                        stations = aux;
                    }
                }

                else {
                    //NO UDDI
                    estacion.Station st = new estacion.Station();

                    String nuevaURL = "http://" + textBox1.Text + "/Practica4/services/Station.StationHttpSoap11Endpoint/";
                    st.Url = nuevaURL;
                    
                    //Prueba de acceso, si la URL no permite acceder a un dato, no se registra
                    st.getLum(out lum, out b);
                    
                    //Si da error al acceder, no registra
                    stations[id] = st;
                    registradas += "Estacion " + (id + 1).ToString() + " -> " + textBox1.Text + "\n";
                    richTextBox1.Text = registradas;
                    id++;

                    comboBox1.Items.Add("Estacion " + id);
                    estacion.Station[] aux = new estacion.Station[id + 1];

                    //Copia el antiguo y lo mete en el nuevo
                    for (int i = 0; i < stations.Length; i++) {
                        aux[i] = stations[i];
                    }
                    stations = new estacion.Station[id + 1];
                    stations = aux;
                }
            }
            catch (Exception t) {
                richTextBox1.Text = t.ToString();// "Se ha producido un error al registrar la nueva estación.";
            }
        }
    }
}
