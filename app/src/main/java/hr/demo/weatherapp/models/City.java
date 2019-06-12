package hr.demo.weatherapp.models;

import java.util.List;

public class City {
        private List<Long> id;
        private List<String> name;
        private List<String> countryCode;

        public List<Long> getId() {
                return id;
        }

        public void setId(List<Long> id) {
                this.id = id;
        }

        public List<String> getName() {
                return name;
        }

        public void setName(List<String> name) {
                this.name = name;
        }

        public List<String> getCountryCode() {
                return countryCode;
        }

        public void setCountryCode(List<String> countryCode) {
                this.countryCode = countryCode;
        }
}
